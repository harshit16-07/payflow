
package com.payflow.transaction.service;

import com.payflow.transaction.entity.Transaction;
import com.payflow.transaction.entity.TransactionStatus;
import com.payflow.transaction.event.PaymentCreatedEvent;
import com.payflow.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void createTransaction(PaymentCreatedEvent event) {

        // Check whether transaction already exists
        if (transactionRepository.findByPaymentId(event.paymentId()).isPresent()) {
            System.out.println(
                    "Transaction already exists for payment: "
                            + event.paymentId()
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
                "Transaction created successfully for payment: "
                        + event.paymentId()
        );
    }
}
