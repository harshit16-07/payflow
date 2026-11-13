package com.payflow.ledger.repository;

import com.payflow.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry,Long> {

    List<LedgerEntry> findByPaymentId(Long PaymentId);
}
