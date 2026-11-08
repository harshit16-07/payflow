package com.payflow.transaction.repository;

import com.payflow.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByPaymentId(Long paymentId);

    Optional<Transaction> findByPaymentReference(String paymentReference);
}