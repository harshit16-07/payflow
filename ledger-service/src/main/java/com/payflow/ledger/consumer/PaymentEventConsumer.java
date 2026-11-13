package com.payflow.ledger.consumer;

import com.payflow.ledger.event.PaymentSucceededEvent;
import com.payflow.ledger.service.LedgerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    public PaymentEventConsumer(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    private final  LedgerService ledgerService;

    @RabbitListener(queues = "payment.succeeded.queue")
    public void consumePaymentSucceededEvent(PaymentSucceededEvent event) {

        System.out.println("Received Payment Succeeded Event!");

        try {
            ledgerService.recordPayment(event);

        } catch (DataIntegrityViolationException exception) {

            if (ledgerService.hasCompleteEntries(event.paymentId())) {

                System.out.println(
                        "Duplicate event safely ignored for payment: "
                                + event.paymentId()
                );

            } else {
                throw exception;
            }
        }
    }


}
