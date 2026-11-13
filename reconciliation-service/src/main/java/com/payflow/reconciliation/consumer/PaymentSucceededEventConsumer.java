package com.payflow.reconciliation.consumer;

import com.payflow.reconciliation.event.PaymentSucceededEvent;
import com.payflow.reconciliation.service.ReconciliationPaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentSucceededEventConsumer {

    private final ReconciliationPaymentService reconciliationPaymentService;

    public PaymentSucceededEventConsumer(ReconciliationPaymentService reconciliationPaymentService) {
        this.reconciliationPaymentService = reconciliationPaymentService;
    }

    @RabbitListener(queues = "payment.succeeded.reconciliation.queue")
    public void reconciliationPaymentSucceededEventConsumer( PaymentSucceededEvent event)
    {
        System.out.println("Event recieved");
        reconciliationPaymentService.saveSuccessfulPayment(event);
    }
}
