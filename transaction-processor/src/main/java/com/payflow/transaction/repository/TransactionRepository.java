package com.payflow.transaction.repository;

import com.payflow.transaction.entity.Transaction;
import com.payflow.transaction.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByPaymentId(Long paymentId);

    Optional<Transaction> findByPaymentReference(String paymentReference);

    @Modifying
    @Query("""
    UPDATE Transaction t
    SET t.status = :newStatus
    WHERE t.paymentId = :paymentId
    AND t.status = :currentStatus
""")
    int updateStatusIfCurrent(
            @Param("paymentId") Long paymentId,
            @Param("currentStatus") TransactionStatus currentStatus,
            @Param("newStatus") TransactionStatus newStatus
    );
}