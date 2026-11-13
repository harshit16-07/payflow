
package com.payflow.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.transaction.entity.OutboxEvent;
import com.payflow.transaction.entity.OutboxStatus;
import com.payflow.transaction.entity.Transaction;
import com.payflow.transaction.entity.TransactionStatus;
import com.payflow.transaction.event.PaymentSucceededEvent;
import com.payflow.transaction.repository.OutboxEventRepository;
import com.payflow.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionStatusService {

    private final TransactionRepository transactionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public TransactionStatusService(
            TransactionRepository transactionRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public boolean markAsProcessingIfInitiated(Long paymentId) {

        int updatedRows = transactionRepository.updateStatusIfCurrent(
                paymentId,
                TransactionStatus.INITIATED,
                TransactionStatus.PROCESSING
        );

        return updatedRows == 1;
    }

    @Transactional
    public void updateStatus(Long paymentId, TransactionStatus status) {

        Transaction transaction = transactionRepository
                .findByPaymentId(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found: " + paymentId));

        boolean newlySuccessful =
                status == TransactionStatus.SUCCESS
                        && transaction.getStatus() != TransactionStatus.SUCCESS;

        transaction.setStatus(status);
        transactionRepository.save(transaction);

        if (newlySuccessful) {

            LocalDateTime completedAt = LocalDateTime.now();

            PaymentSucceededEvent event = new PaymentSucceededEvent(
                    transaction.getPaymentId(),
                    transaction.getPaymentReference(),
                    transaction.getAmount(),
                    transaction.getCurrency(),
                    completedAt
            );

            try {
                OutboxEvent outboxEvent = new OutboxEvent();

                outboxEvent.setAggregateId(paymentId);
                outboxEvent.setEventType("PAYMENT_SUCCEEDED");
                outboxEvent.setPayload(
                        objectMapper.writeValueAsString(event)
                );
                outboxEvent.setStatus(OutboxStatus.PENDING);
                outboxEvent.setCreatedAt(completedAt);

                outboxEventRepository.save(outboxEvent);

                System.out.println(
                        "Payment succeeded event saved to outbox: " + paymentId
                );

            } catch (JsonProcessingException exception) {
                throw new IllegalStateException(
                        "Failed to serialize PaymentSucceededEvent",
                        exception
                );
            }
        }
    }
}
