
package com.payflow.reconciliation.service;

import com.payflow.reconciliation.entity.ReconciliationPayment;
import com.payflow.reconciliation.event.PaymentSucceededEvent;
import com.payflow.reconciliation.repository.ReconciliationPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReconciliationPaymentService {

    private final ReconciliationPaymentRepository repository;

    public ReconciliationPaymentService(
            ReconciliationPaymentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveSuccessfulPayment(PaymentSucceededEvent event) {

        // Check whether this payment was already recorded
        if (repository.findByPaymentId(event.paymentId()).isPresent()) {
            System.out.println(
                    "Payment already recorded for reconciliation: "
                            + event.paymentId()
            );
            return;
        }

        ReconciliationPayment payment = new ReconciliationPayment();

        payment.setPaymentId(event.paymentId());
        payment.setPaymentReference(event.paymentReference());
        payment.setAmount(event.amount());
        payment.setCurrency(event.currency());
        payment.setInternalStatus("SUCCESS");
        payment.setSucceededAt(event.completedAt());

        repository.save(payment);

        System.out.println(
                "Payment saved for reconciliation: " + event.paymentId()
        );
    }
}
