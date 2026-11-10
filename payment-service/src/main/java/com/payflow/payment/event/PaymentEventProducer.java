package com.payflow.payment.event;

import com.payflow.payment.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {
    private final RabbitTemplate rabbitTemplate;

    public PaymentEventProducer( RabbitTemplate rabbitTemplate)
    {
        this.rabbitTemplate=rabbitTemplate;
    }

    public void publishPaymentCreatedEvent(String payload)
    {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                payload
        );
    }

}
