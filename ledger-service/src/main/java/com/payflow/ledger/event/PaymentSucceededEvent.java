package com.payflow.ledger.event;



import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSucceededEvent(
        Long paymentId,
        String paymentReference,
        BigDecimal amount,
        String currency,
        LocalDateTime completedAt
) {}