package com.payflow.transaction.event;

import com.payflow.transaction.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentSucceededEventProducer {

    public final RabbitTemplate rabbitTemplate;

    public PaymentSucceededEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public  void publishPaymentSucceededEvent(PaymentSucceededEvent event)
    {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE,RabbitMQConfig.ROUTING_KEY,event);
    }
}
