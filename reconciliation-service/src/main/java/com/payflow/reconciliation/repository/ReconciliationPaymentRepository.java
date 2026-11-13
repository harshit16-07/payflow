package com.payflow.reconciliation.repository;

import com.payflow.reconciliation.entity.ReconciliationPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReconciliationPaymentRepository extends JpaRepository<ReconciliationPayment,Long> {

    Optional<ReconciliationPayment> findByPaymentId(Long paymentId);
    Optional<ReconciliationPayment> findByPaymentReference(String paymentReference);
}
