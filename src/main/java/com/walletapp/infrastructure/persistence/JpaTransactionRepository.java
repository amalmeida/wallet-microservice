package com.walletapp.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    boolean existsByIdempotencyKey(UUID idempotencyKey);
    List<TransactionEntity> findByWalletIdAndTimestampBefore(UUID walletId, LocalDateTime timestamp);
}