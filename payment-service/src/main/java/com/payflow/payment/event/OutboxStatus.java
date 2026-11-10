package com.payflow.payment.event;

public enum OutboxStatus {


    // Event is saved in PostgreSQL but not yet published to RabbitMQ
    PENDING,

    // Event has been successfully published to RabbitMQ
    PUBLISHED
}
