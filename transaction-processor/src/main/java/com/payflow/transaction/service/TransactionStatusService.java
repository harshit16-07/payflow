package com.payflow.transaction.service;

import com.payflow.transaction.entity.Transaction;
import com.payflow.transaction.entity.TransactionStatus;
import com.payflow.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionStatusService {

    private final TransactionRepository transactionRepository;

    public TransactionStatusService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void updateStatus(Long paymentId, TransactionStatus status) {

        Transaction transaction = transactionRepository
                .findByPaymentId(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found: " + paymentId));

        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }
}
