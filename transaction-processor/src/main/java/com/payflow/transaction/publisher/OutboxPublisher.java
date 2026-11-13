package com.payflow.transaction.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.transaction.entity.OutboxEvent;
import com.payflow.transaction.entity.OutboxStatus;
import com.payflow.transaction.event.PaymentSucceededEvent;
import com.payflow.transaction.event.PaymentSucceededEventProducer;
import com.payflow.transaction.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final PaymentSucceededEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            PaymentSucceededEventProducer eventProducer,
            ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> pendingEvents =
                outboxEventRepository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEvent outboxEvent : pendingEvents) {
            try {
                PaymentSucceededEvent event = objectMapper.readValue(
                        outboxEvent.getPayload(),
                        PaymentSucceededEvent.class
                );

                eventProducer.publishPaymentSucceededEvent(event);

                outboxEvent.setStatus(OutboxStatus.PUBLISHED);
                outboxEventRepository.save(outboxEvent);

                System.out.println(
                        "Payment succeeded event published: "
                                + outboxEvent.getId()
                );

            } catch (Exception exception) {
                System.out.println(
                        "Failed to publish outbox event: "
                                + outboxEvent.getId()
                );
                exception.printStackTrace();
            }
        }
    }
}