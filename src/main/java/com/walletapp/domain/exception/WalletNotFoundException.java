package com.walletapp.domain.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(UUID walletId) {
        super("Carteira não encontrada: " + walletId);
    }
}