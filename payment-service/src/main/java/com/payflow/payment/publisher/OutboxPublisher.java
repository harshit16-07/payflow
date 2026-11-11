package com.payflow.payment.publisher;

import com.payflow.payment.entity.OutboxEvent;
import com.payflow.payment.event.OutboxStatus;
import com.payflow.payment.event.PaymentEventProducer;
import com.payflow.payment.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import com.payflow.payment.event.PaymentCreatedEvent;
import java.util.List;

@Component
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository, PaymentEventProducer paymentEventProducer, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.paymentEventProducer = paymentEventProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents()
    {
        List<OutboxEvent> pendingEvents= outboxEventRepository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEvent outboxEvent : pendingEvents) {

            try {
                // Convert stored JSON into a Java event object
                PaymentCreatedEvent event = objectMapper.readValue(
                        outboxEvent.getPayload(),
                        PaymentCreatedEvent.class
                );

                // Publish the event to RabbitMQ
                paymentEventProducer.publishPaymentCreatedEvent(event);
                // Mark event as successfully published
                outboxEvent.setStatus(OutboxStatus.PUBLISHED);

// Save updated status in PostgreSQL
                outboxEventRepository.save(outboxEvent);

                System.out.println(
                        "Event published successfully: " + outboxEvent.getId()
                );

            } catch (Exception exception) {

                System.out.println(
                        "Failed to publish event: " + outboxEvent.getId()
                );

                exception.printStackTrace();
            }
        }

    }
}
