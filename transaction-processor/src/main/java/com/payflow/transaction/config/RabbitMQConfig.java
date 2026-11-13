package com.payflow.transaction.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE="payment.exchange";
    public static final String ROUTING_KEY = "payment.succeeded";

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter()
    {
        return new JacksonJsonMessageConverter();
    }
}
