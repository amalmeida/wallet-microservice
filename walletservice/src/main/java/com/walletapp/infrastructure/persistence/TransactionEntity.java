package com.walletapp.infrastructure.persistence;

import com.walletapp.domain.model.Transaction;
import com.walletapp.domain.model.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class TransactionEntity {
    @Id
    private UUID id;
    private UUID walletId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private LocalDateTime timestamp;
    private UUID idempotencyKey;

    public static TransactionEntity fromDomain(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.id());
        entity.setWalletId(transaction.walletId());
        entity.setAmount(transaction.amount());
        entity.setType(transaction.type());
        entity.setTimestamp(transaction.timestamp());
        entity.setIdempotencyKey(transaction.idempotencyKey());
        return entity;
    }
}