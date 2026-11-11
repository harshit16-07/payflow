package com.payflow.ledger.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private  static final String EXCHANGE="payment.exchange";
    private  static final String QUEUE="payment.succeeded.queue";
    private  static final  String ROUTING_KEY="payment.succeeded";

    @Bean
    public DirectExchange paymentExchange()
    {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue paymentSucceededQueue()
    {
        return new Queue(QUEUE,true);
    }

    @Bean
    public Binding paymentSucceededBinding(){
        return BindingBuilder.bind(paymentSucceededQueue()).to(paymentExchange()).with(ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter()
    {
        return new JacksonJsonMessageConverter();
    }
}
