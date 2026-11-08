
package com.payflow.transaction.consumer;

import com.payflow.transaction.event.PaymentCreatedEvent;
import com.payflow.transaction.service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private final TransactionService transactionService;

    public PaymentEventConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RabbitListener(queues = "payment.queue")
    public void consumePaymentCreatedEvent(PaymentCreatedEvent event) {

        System.out.println("Received Payment Created Event!");

        transactionService.createTransaction(event);
    }
}
