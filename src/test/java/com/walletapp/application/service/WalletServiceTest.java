package com.walletapp.application.service;

import com.walletapp.domain.model.Transaction;
import com.walletapp.domain.model.TransactionType;
import com.walletapp.domain.model.Wallet;
import com.walletapp.domain.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private UUID walletId;
    private UUID userId;
    private UUID idempotencyKey;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        userId = UUID.randomUUID();
        idempotencyKey = UUID.randomUUID();
    }

    @Test
    void createWallet_ShouldReturnWalletId() {
        Wallet wallet = new Wallet(userId);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        UUID result = walletService.createWallet(userId);

        assertNotNull(result);
        assertEquals(wallet.getId(), result);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void retrieveBalance_ShouldReturnBalance_WhenWalletExists() {
        Wallet wallet = new Wallet(walletId, userId, BigDecimal.valueOf(100), LocalDateTime.now());
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.retrieveBalance(walletId);

        assertEquals(BigDecimal.valueOf(100), balance);
        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    void retrieveBalance_ShouldThrowException_WhenWalletNotFound() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            walletService.retrieveBalance(walletId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Carteira não encontrada", exception.getReason());
        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    void depositFunds_ShouldIncreaseBalance_WhenNotProcessed() {
        Wallet wallet = new Wallet(walletId, userId, BigDecimal.ZERO, LocalDateTime.now());
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.hasProcessed(idempotencyKey)).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        walletService.depositFunds(walletId, BigDecimal.valueOf(50), idempotencyKey);

        assertEquals(BigDecimal.valueOf(50), wallet.getBalance());
        verify(walletRepository, times(1)).recordTransaction(any(Transaction.class));
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void depositFunds_ShouldDoNothing_WhenAlreadyProcessed() {
        when(walletRepository.hasProcessed(idempotencyKey)).thenReturn(true);

        walletService.depositFunds(walletId, BigDecimal.valueOf(50), idempotencyKey);

        verify(walletRepository, never()).findById(walletId);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void transferFunds_ShouldTransferAmount_WhenValid() {
        Wallet fromWallet = new Wallet(walletId, userId, BigDecimal.valueOf(100), LocalDateTime.now());
        Wallet toWallet = new Wallet(UUID.randomUUID(), userId, BigDecimal.ZERO, LocalDateTime.now());
        when(walletRepository.findById(fromWallet.getId())).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findById(toWallet.getId())).thenReturn(Optional.of(toWallet));
        when(walletRepository.hasProcessed(idempotencyKey)).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        walletService.transferFunds(fromWallet.getId(), toWallet.getId(), BigDecimal.valueOf(50), idempotencyKey);

        assertEquals(BigDecimal.valueOf(50), fromWallet.getBalance());
        assertEquals(BigDecimal.valueOf(50), toWallet.getBalance());
        verify(walletRepository, times(2)).recordTransaction(any(Transaction.class));
        verify(walletRepository, times(2)).save(any(Wallet.class));
    }

    @Test
    void transferFunds_ShouldThrowException_WhenSameWallet() {
        assertThrows(IllegalArgumentException.class, () -> {
            walletService.transferFunds(walletId, walletId, BigDecimal.valueOf(50), idempotencyKey);
        });
        verify(walletRepository, never()).findById(any());
    }
}