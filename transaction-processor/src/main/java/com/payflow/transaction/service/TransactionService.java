
package com.payflow.transaction.service;

import com.payflow.transaction.entity.Transaction;
import com.payflow.transaction.entity.TransactionStatus;
import com.payflow.transaction.event.PaymentCreatedEvent;
import com.payflow.transaction.repository.TransactionRepository;
import com.payflow.transaction.provider.PaymentProviderSimulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentProviderSimulator paymentProviderSimulator;
    private final TransactionStatusService transactionStatusService;

    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_DELAY_MS = 1000L;

    public TransactionService(
            TransactionRepository transactionRepository,
            PaymentProviderSimulator paymentProviderSimulator,
            TransactionStatusService transactionStatusService) {

        this.transactionRepository = transactionRepository;
        this.paymentProviderSimulator = paymentProviderSimulator;
        this.transactionStatusService = transactionStatusService;
    }

    @Transactional
    public void createTransaction(PaymentCreatedEvent event) {

        // Check whether transaction already exists
        if (transactionRepository.findByPaymentId(event.paymentId()).isPresent()) {
            System.out.println(
                    "Transaction already exists for payment: " + event.paymentId()
            );
            return;
        }

        // Create a new transaction
        Transaction transaction = new Transaction();

        transaction.setPaymentId(event.paymentId());
        transaction.setPaymentReference(event.paymentReference());
        transaction.setAmount(event.amount());
        transaction.setCurrency(event.currency());
        transaction.setStatus(TransactionStatus.INITIATED);
        transaction.setCreatedAt(event.createdAt());

        // Save transaction in PostgreSQL
        transactionRepository.save(transaction);

        System.out.println(
                "Transaction created successfully for payment: " + event.paymentId()
        );
    }

    public void processTransaction(Long paymentId) {

        Transaction transaction = transactionRepository
                .findByPaymentId(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found: " + paymentId));

        if (transaction.getStatus() != TransactionStatus.INITIATED) {
            System.out.println("Transaction cannot be processed: " + paymentId);
            return;
        }

        // 1. Commit PROCESSING status
        transactionStatusService.updateStatus(
                paymentId, TransactionStatus.PROCESSING);

        // 2. Process payment with retries
        int attempts = 0;
        boolean paymentSuccessful = false;

        while (attempts < MAX_ATTEMPTS) {

            attempts++;

            try {
                paymentSuccessful = paymentProviderSimulator.processPayment();

                System.out.println("Payment provider attempt: " + attempts);
                break;

            } catch (Exception e) {

                System.out.println(
                        "Attempt " + attempts + " failed: " + e.getMessage()
                );

                if (attempts == MAX_ATTEMPTS) {

                    transactionStatusService.updateStatus(
                            paymentId, TransactionStatus.FAILED);

                    System.out.println(
                            "All payment attempts exhausted: " + paymentId
                    );
                    return;
                }

                // Exponential backoff
                long delay = INITIAL_DELAY_MS * (1L << (attempts - 1));

                try {
                    Thread.sleep(delay);

                } catch (InterruptedException interruptedException) {

                    Thread.currentThread().interrupt();

                    transactionStatusService.updateStatus(
                            paymentId, TransactionStatus.FAILED);

                    System.out.println("Retry interrupted: " + paymentId);
                    return;
                }
            }
        }

        // 3. Update final transaction status
        if (paymentSuccessful) {

            transactionStatusService.updateStatus(
                    paymentId, TransactionStatus.SUCCESS);

            System.out.println("Payment successful: " + paymentId);

        } else {

            transactionStatusService.updateStatus(
                    paymentId, TransactionStatus.FAILED);

            System.out.println("Payment failed: " + paymentId);
        }
    }
}
