package com.walletapp.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(
        UUID id,
        UUID walletId,
        BigDecimal amount,
        TransactionType type,
        LocalDateTime timestamp,
        UUID idempotencyKey // Client-provided key to ensure idempotency
) {
    public Transaction(UUID walletId, BigDecimal amount, TransactionType type, UUID idempotencyKey) {
        this(UUID.randomUUID(), walletId, amount, type, LocalDateTime.now(), idempotencyKey);
    }
}

