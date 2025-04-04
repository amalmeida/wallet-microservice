package com.walletapp.application.service;

import com.walletapp.domain.exception.InvalidDateFormatException;
import com.walletapp.domain.exception.WalletNotFoundException;
import com.walletapp.domain.model.Transaction;
import com.walletapp.domain.model.TransactionType;
import com.walletapp.domain.model.Wallet;
import com.walletapp.domain.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
public class WalletService {
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public UUID createWallet(UUID userId) {
        logger.info("Criando carteira para userId: {}", userId);
        Wallet wallet = new Wallet(userId);
        Wallet savedWallet = walletRepository.save(wallet);
        logger.debug("Carteira criada com ID: {}", savedWallet.getId());
        return savedWallet.getId();
    }

    public BigDecimal retrieveBalance(UUID walletId) {
        logger.debug("Consultando saldo da carteira: {}", walletId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.error("Carteira não encontrada: {}", walletId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Carteira não encontrada");
                });
        return wallet.getBalance();
    }

    public BigDecimal retrieveHistoricalBalance(UUID walletId, String dateTime) {
        logger.info("Consultando saldo histórico da carteira: {} em {}", walletId, dateTime);

        LocalDateTime at;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss][\'T\'HH:mm:ss]");
            at = LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException e) {
            logger.error("Formato de data inválido: {}. Use o formato 'yyyy-MM-dd HH:mm:ss' ou 'yyyy-MM-ddTHH:mm:ss'", dateTime);
            throw new InvalidDateFormatException("Formato de data inválido. Use 'yyyy-MM-dd HH:mm:ss' ou 'yyyy-MM-ddTHH:mm:ss'");
        }

        walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        return walletRepository.getHistoricalBalance(walletId, at);
    }

    @Transactional
    public void depositFunds(UUID walletId, BigDecimal amount, UUID idempotencyKey) {
        logger.info("Depositando {} na carteira: {} (idempotencyKey: {})", amount, walletId, idempotencyKey);
        if (walletRepository.hasProcessed(idempotencyKey)) {
            logger.info("Operação já processada com idempotencyKey: {}", idempotencyKey);
            return;
        }
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.error("Carteira não encontrada: {}", walletId);
                    return new IllegalArgumentException("Carteira não encontrada");
                });
        wallet.deposit(amount);
        walletRepository.save(wallet);
        walletRepository.recordTransaction(new Transaction(walletId, amount, TransactionType.DEPOSIT, idempotencyKey));
        logger.debug("Depósito concluído para carteira: {}", walletId);
    }

    @Transactional
    public void withdrawFunds(UUID walletId, BigDecimal amount, UUID idempotencyKey) {
        logger.info("Sacando {} da carteira: {} (idempotencyKey: {})", amount, walletId, idempotencyKey);
        if (walletRepository.hasProcessed(idempotencyKey)) {
            logger.info("Operação já processada com idempotencyKey: {}", idempotencyKey);
            return;
        }
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.error("Carteira não encontrada: {}", walletId);
                    return new IllegalArgumentException("Carteira não encontrada");
                });
        wallet.withdraw(amount);
        walletRepository.save(wallet);
        walletRepository.recordTransaction(new Transaction(walletId, amount, TransactionType.WITHDRAWAL, idempotencyKey));
        logger.debug("Saque concluído para carteira: {}", walletId);
    }

    @Transactional
    public void transferFunds(UUID fromWalletId, UUID toWalletId, BigDecimal amount, UUID idempotencyKey) {
        logger.info("Transferindo {} de {} para {} (idempotencyKey: {})", amount, fromWalletId, toWalletId, idempotencyKey);
        if (fromWalletId.equals(toWalletId)) {
            logger.error("Carteiras de origem e destino não podem ser iguais");
            throw new IllegalArgumentException("Carteiras de origem e destino não podem ser iguais");
        }
        if (walletRepository.hasProcessed(idempotencyKey)) {
            logger.info("Operação já processada com idempotencyKey: {}", idempotencyKey);
            return;
        }
        Wallet fromWallet = walletRepository.findById(fromWalletId)
                .orElseThrow(() -> {
                    logger.error("Carteira de origem não encontrada: {}", fromWalletId);
                    return new IllegalArgumentException("Carteira de origem não encontrada");
                });
        Wallet toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() -> {
                    logger.error("Carteira de destino não encontrada: {}", toWalletId);
                    return new IllegalArgumentException("Carteira de destino não encontrada");
                });

        fromWallet.withdraw(amount);
        toWallet.deposit(amount);

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);
        walletRepository.recordTransaction(new Transaction(fromWalletId, amount, TransactionType.TRANSFER_SENT, idempotencyKey));
        walletRepository.recordTransaction(new Transaction(toWalletId, amount, TransactionType.TRANSFER_RECEIVED, idempotencyKey));
        logger.debug("Transferência concluída de {} para {}", fromWalletId, toWalletId);
    }
}