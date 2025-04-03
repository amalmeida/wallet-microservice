package com.walletapp.infrastructure.persistence;

import com.walletapp.domain.model.Wallet;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class WalletEntity {
    @Id
    private UUID id;
    private UUID userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public static WalletEntity fromDomain(Wallet wallet) {
        WalletEntity entity = new WalletEntity();
        entity.setId(wallet.getId());
        entity.setUserId(wallet.getUserId());
        entity.setBalance(wallet.getBalance());
        entity.setCreatedAt(wallet.getCreatedAt());
        return entity;
    }

    public Wallet toDomain() {
        return new Wallet(this.id, this.userId, this.balance, this.createdAt);
    }
}