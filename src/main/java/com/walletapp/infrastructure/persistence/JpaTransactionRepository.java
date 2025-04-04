package com.walletapp.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    boolean existsByIdempotencyKey(UUID idempotencyKey);
   // List<TransactionEntity> findByWalletIdAndTimestampBefore(UUID walletId, LocalDateTime timestamp);
    @Query("SELECT t FROM TransactionEntity t WHERE t.walletId = :walletId AND t.timestamp <= :dateTime ORDER BY t.timestamp ASC")
    List<TransactionEntity> findByWalletIdAndTimestampBefore(@Param("walletId") UUID walletId, @Param("dateTime") LocalDateTime dateTime);

}