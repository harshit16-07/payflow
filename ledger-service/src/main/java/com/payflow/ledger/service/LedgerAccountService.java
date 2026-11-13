package com.payflow.ledger.service;

import com.payflow.ledger.entity.AccountType;
import com.payflow.ledger.entity.LedgerAccount;
import com.payflow.ledger.repository.AccountBalanceProjection;
import com.payflow.ledger.repository.LedgerAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LedgerAccountService {

    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerAccountService(
            LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    @Transactional
    public LedgerAccount createAccount(
            String accountNumber,
            AccountType accountType,
            String currency,
            BigDecimal openingBalance) {

        if (ledgerAccountRepository
                .findByAccountNumber(accountNumber).isPresent()) {
            throw new IllegalArgumentException(
                    "Account already exists: " + accountNumber);
        }

        LedgerAccount account = new LedgerAccount();
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setCurrency(currency);
        account.setOpeningBalance(openingBalance);
        account.setCreatedAt(LocalDateTime.now());

        return ledgerAccountRepository.save(account);
    }


    @Transactional
    public LedgerAccount createAccountIfAbsent(
            String accountNumber,
            AccountType accountType,
            String currency,
            BigDecimal openingBalance) {

        return ledgerAccountRepository
                .findByAccountNumber(accountNumber)
                .orElseGet(() -> createAccount(
                        accountNumber,
                        accountType,
                        currency,
                        openingBalance
                ));
    }

    @Transactional(readOnly = true)
    public List<AccountBalanceProjection> getAllAccountBalances() {
        return ledgerAccountRepository.findAllAccountBalances();
    }
}