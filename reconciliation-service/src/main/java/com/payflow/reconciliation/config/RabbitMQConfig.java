package com.payflow.reconciliation.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String EXCHANGE= "payment.exchange";
    private static  final String QUEUE  ="payment.succeeded.reconciliation.queue";
    private  static final String ROUTING_KEY="payment.succeeded";

    @Bean
    public DirectExchange paymentExchange()
    {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue reconciliationQueue()
    {
        return  new Queue(QUEUE,true);
    }

    @Bean
    public Binding reconciliationBinding()
    {
        return  BindingBuilder.bind(reconciliationQueue()).to(paymentExchange()).with(ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter()
    {
        return new JacksonJsonMessageConverter();
    }

}
