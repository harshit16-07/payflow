package com.payflow.payment.config;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE="payment.exchange";
    public static final String QUEUE="payment.queue";
    public static  final String ROUTING_KEY = "payment.created";

    @Bean
    public DirectExchange paymentExchange()
    {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue paymentQueue()
    {
        return new Queue(QUEUE,true);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Binding paymentBinding()
    {
        return BindingBuilder.bind(paymentQueue()).to(paymentExchange()).with(ROUTING_KEY);
    }
}
