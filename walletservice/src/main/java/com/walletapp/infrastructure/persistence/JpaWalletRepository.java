package com.walletapp.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface JpaWalletRepository extends JpaRepository<WalletEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WalletEntity> findById(UUID walletId);
}