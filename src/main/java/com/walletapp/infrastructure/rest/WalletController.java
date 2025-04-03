package com.walletapp.infrastructure.rest;

import com.walletapp.application.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public UUID createWallet(@RequestParam UUID userId) {
        logger.info("Requisição para criar carteira para userId: {}", userId);
        return walletService.createWallet(userId);
    }

    @GetMapping("/{walletId}/balance")
    public BigDecimal retrieveBalance(@PathVariable UUID walletId) {
        logger.debug("Requisição para consultar saldo da carteira: {}", walletId);
        return walletService.retrieveBalance(walletId);
    }

    @GetMapping("/{walletId}/historical-balance")
    public BigDecimal retrieveHistoricalBalance(
            @PathVariable UUID walletId,
            @RequestParam("at") String dateTime
    ) {
        LocalDateTime at = LocalDateTime.parse(dateTime);
        return walletService.retrieveHistoricalBalance(walletId, at);
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<Void> depositFunds(
            @PathVariable UUID walletId,
            @RequestParam BigDecimal amount,
            @RequestHeader("Idempotency-Key") UUID idempotencyKey
    ) {
        walletService.depositFunds(walletId, amount, idempotencyKey);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<Void> withdrawFunds(
            @PathVariable UUID walletId,
            @RequestParam BigDecimal amount,
            @RequestHeader("Idempotency-Key") UUID idempotencyKey
    ) {
        walletService.withdrawFunds(walletId, amount, idempotencyKey);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferFunds(
            @RequestParam UUID fromWalletId,
            @RequestParam UUID toWalletId,
            @RequestParam BigDecimal amount,
            @RequestHeader("Idempotency-Key") UUID idempotencyKey
    ) {
        walletService.transferFunds(fromWalletId, toWalletId, amount, idempotencyKey);
        return ResponseEntity.ok().build();
    }
}