package com.walletapp.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Wallet {
    private final UUID id;
    private final UUID userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public Wallet(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    // Construtor para conversão de entidade
    public Wallet(UUID id, UUID userId, BigDecimal balance, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Valor de saque inválido");
        }
        this.balance = this.balance.subtract(amount);
    }

}