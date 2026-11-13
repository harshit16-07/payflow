
package com.payflow.ledger.service;

import com.payflow.ledger.entity.LedgerAccount;
import com.payflow.ledger.entity.LedgerEntry;
import com.payflow.ledger.entity.LedgerEntryType;
import com.payflow.ledger.event.PaymentSucceededEvent;
import com.payflow.ledger.repository.LedgerAccountRepository;
import com.payflow.ledger.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerService(
            LedgerEntryRepository ledgerEntryRepository,
            LedgerAccountRepository ledgerAccountRepository) {

        this.ledgerEntryRepository = ledgerEntryRepository;
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    @Transactional
    public void recordPayment(PaymentSucceededEvent event) {

        // Prevent duplicate ledger entries
        if (!ledgerEntryRepository.findByPaymentId(event.paymentId()).isEmpty()) {
            System.out.println(
                    "Ledger entries already exist for payment: "
                            + event.paymentId()
            );
            return;
        }

        // Fetch customer account from database
        LedgerAccount customerAccount = ledgerAccountRepository
                .findByAccountNumber("CUSTOMER-101")
                .orElseThrow(() ->
                        new RuntimeException("Customer account not found"));

        // Fetch merchant account from database
        LedgerAccount merchantAccount = ledgerAccountRepository
                .findByAccountNumber("MERCHANT-201")
                .orElseThrow(() ->
                        new RuntimeException("Merchant account not found"));

        // Validate account currencies
        if (!customerAccount.getCurrency().equalsIgnoreCase(event.currency())
                || !merchantAccount.getCurrency().equalsIgnoreCase(event.currency())) {

            throw new IllegalArgumentException(
                    "Account currency does not match payment currency"
            );
        }

        // Create customer debit entry
        LedgerEntry debitEntry = new LedgerEntry();
        debitEntry.setPaymentId(event.paymentId());
        debitEntry.setAccountId(customerAccount.getAccountNumber());
        debitEntry.setEntryType(LedgerEntryType.DEBIT);
        debitEntry.setAmount(event.amount());
        debitEntry.setCurrency(event.currency());
        debitEntry.setCreatedAt(LocalDateTime.now());

        // Create merchant credit entry
        LedgerEntry creditEntry = new LedgerEntry();
        creditEntry.setPaymentId(event.paymentId());
        creditEntry.setAccountId(merchantAccount.getAccountNumber());
        creditEntry.setEntryType(LedgerEntryType.CREDIT);
        creditEntry.setAmount(event.amount());
        creditEntry.setCurrency(event.currency());
        creditEntry.setCreatedAt(LocalDateTime.now());

        // Save both entries within the same transaction
        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);

        System.out.println(
                "Ledger entries created for payment: "
                        + event.paymentId()
        );
    }

    @Transactional(readOnly = true)
    public boolean hasCompleteEntries(Long paymentId) {

        List<LedgerEntry> entries =
                ledgerEntryRepository.findByPaymentId(paymentId);

        boolean hasDebit = entries.stream()
                .anyMatch(entry ->
                        entry.getEntryType() == LedgerEntryType.DEBIT);

        boolean hasCredit = entries.stream()
                .anyMatch(entry ->
                        entry.getEntryType() == LedgerEntryType.CREDIT);

        return entries.size() == 2 && hasDebit && hasCredit;
    }
}
