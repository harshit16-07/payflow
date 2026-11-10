
package com.payflow.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.payflow.payment.exception.PaymentNotFoundException;
import com.payflow.payment.entity.Payment;
import com.payflow.payment.entity.PaymentStatus;
import com.payflow.payment.entity.OutboxEvent;
import com.payflow.payment.event.OutboxStatus;
import com.payflow.payment.repository.PaymentRepository;
import com.payflow.payment.repository.OutboxEventRepository;
import com.payflow.payment.event.PaymentCreatedEvent;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    // Constructor injection for required dependencies
    public PaymentService(
            PaymentRepository paymentRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper) {

        this.paymentRepository = paymentRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    // Saves payment and its corresponding outbox event
    // in one database transaction
    @Transactional
    public Payment createPayment(Payment payment) {

        // 1. Check whether this request was already processed
        Payment existingPayment = paymentRepository
                .findByIdempotencyKey(payment.getIdempotencyKey())
                .orElse(null);

        if (existingPayment != null) {

            // 2. Validate that the request details match
            if (existingPayment.getAmount().compareTo(payment.getAmount()) != 0
                    || !existingPayment.getCurrency()
                    .equalsIgnoreCase(payment.getCurrency())) {

                throw new IllegalArgumentException(
                        "Idempotency key already used with different payment details"
                );
            }

            System.out.println(
                    "Duplicate request detected. Returning existing payment: "
                            + existingPayment.getPaymentReference()
            );

            return existingPayment;
        }

        // 3. Create a new payment
        payment.setPaymentReference("PAY-" + UUID.randomUUID());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        // 4. Save payment in PostgreSQL
        Payment savedPayment = paymentRepository.save(payment);

        // 5. Create payment event
        PaymentCreatedEvent event = new PaymentCreatedEvent(
                savedPayment.getId(),
                savedPayment.getPaymentReference(),
                savedPayment.getAmount(),
                savedPayment.getCurrency(),
                savedPayment.getCreatedAt()
        );

        // 6. Create an outbox event record
        OutboxEvent outboxEvent = new OutboxEvent();

        // Connect this event to its payment
        outboxEvent.setAggregateId(savedPayment.getId());

        // Identify the type of event
        outboxEvent.setEventType("PAYMENT_CREATED");

        // Convert the Java event object into JSON
        try {
            outboxEvent.setPayload(
                    objectMapper.writeValueAsString(event)
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Failed to serialize payment event",
                    exception
            );
        }

        // Mark the event as waiting for publication
        outboxEvent.setStatus(OutboxStatus.PENDING);

        // Record when the event was created
        outboxEvent.setCreatedAt(LocalDateTime.now());

        // 7. Save the event in PostgreSQL
        outboxEventRepository.save(outboxEvent);

        // 8. Return the newly created payment
        return savedPayment;
    }

    // Retrieves all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Retrieves payment using its database ID
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with id : " + id
                ));
    }

    // Retrieves payment using its payment reference
    public Payment getPaymentByReference(String paymentReference) {
        return paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with reference : " + paymentReference
                ));
    }
}
