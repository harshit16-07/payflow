
package com.payflow.payment.entity;

import com.payflow.payment.event.OutboxStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    // Unique primary key for each outbox event
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of the payment associated with this event
    @Column(nullable = false)
    private Long aggregateId;

    // Type of event that needs to be published to RabbitMQ
    // Example: PAYMENT_CREATED
    @Column(nullable = false)
    private String eventType;

    // Stores the event data in JSON format
    // Example: paymentId, paymentReference, payment amount, currency
    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    // Tracks the publishing state of this event
    // Possible values: PENDING, PUBLISHED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    // Records when this event was created
    @Column(nullable = false)
    private LocalDateTime createdAt;


    // Returns the unique ID of the outbox event
    public Long getId() {
        return id;
    }

    // Sets the unique ID of the outbox event
    public void setId(Long id) {
        this.id = id;
    }

    // Returns the ID of the associated payment
    public Long getAggregateId() {
        return aggregateId;
    }

    // Sets the ID of the associated payment
    public void setAggregateId(Long aggregateId) {
        this.aggregateId = aggregateId;
    }

    // Returns the event type
    public String getEventType() {
        return eventType;
    }

    // Sets the event type
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    // Returns the JSON event payload
    public String getPayload() {
        return payload;
    }

    // Sets the JSON event payload
    public void setPayload(String payload) {
        this.payload = payload;
    }

    // Returns the current publishing status
    public OutboxStatus getStatus() {
        return status;
    }

    // Updates the publishing status
    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    // Returns the event creation timestamp
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Sets the event creation timestamp
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
