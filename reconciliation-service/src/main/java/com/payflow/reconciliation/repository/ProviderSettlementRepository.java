package com.payflow.reconciliation.repository;

import com.payflow.reconciliation.entity.ProviderSettlement;
import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderSettlementRepository extends JpaRepository<ProviderSettlement,Long> {

    Optional<ProviderSettlement> findByProviderTransactionId(
            String providerTransactionId
    );

    Optional<ProviderSettlement> findByPaymentReference(
            String paymentReference
    );
}
