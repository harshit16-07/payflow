package com.payflow.transaction.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSucceededEvent(
        Long paymentId,
        String paymentReference,
        BigDecimal amount,
        String currency,
        LocalDateTime completedAt
) {}