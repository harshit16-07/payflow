package com.payflow.ledger.consumer;

import com.payflow.ledger.event.PaymentSucceededEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    @RabbitListener(queues = "payment.succeeded.queue")
    public void consumePaymentSucceededEvent(PaymentSucceededEvent event)
    {
        System.out.println("Received Payment Succeeded Event!");
        System.out.println("Payment ID: " + event.paymentId());
        System.out.println("Payment Reference: " + event.paymentReference());
        System.out.println("Amount: " + event.amount());
        System.out.println("Currency: " + event.currency());
    }
}
