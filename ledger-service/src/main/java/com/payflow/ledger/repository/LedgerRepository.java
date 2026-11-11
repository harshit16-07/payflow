package com.payflow.ledger.repository;

import com.payflow.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerRepository extends JpaRepository<LedgerEntry,Long> {

    Optional<LedgerEntry> findByPaymentId(Long PaymentId);
}
