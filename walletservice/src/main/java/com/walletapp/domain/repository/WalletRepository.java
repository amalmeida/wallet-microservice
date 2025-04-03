package com.walletapp.domain.repository;

import com.walletapp.domain.model.Wallet;
import com.walletapp.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(UUID walletId);
    BigDecimal getHistoricalBalance(UUID walletId, LocalDateTime dateTime);
    void recordTransaction(Transaction transaction);
    boolean hasProcessed(UUID idempotencyKey);
}