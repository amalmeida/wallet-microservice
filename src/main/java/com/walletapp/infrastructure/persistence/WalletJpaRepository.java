package com.walletapp.infrastructure.persistence;

import com.walletapp.domain.model.Transaction;
import com.walletapp.domain.model.Wallet;
import com.walletapp.domain.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletJpaRepository implements WalletRepository {
    private final JpaWalletRepository jpaWalletRepository;
    private final JpaTransactionRepository jpaTransactionRepository;

    public WalletJpaRepository(JpaWalletRepository jpaWalletRepository, JpaTransactionRepository jpaTransactionRepository) {
        this.jpaWalletRepository = jpaWalletRepository;
        this.jpaTransactionRepository = jpaTransactionRepository;
    }

    @Transactional
    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = WalletEntity.fromDomain(wallet);
        WalletEntity savedEntity = jpaWalletRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Transactional
    @Override
    public Optional<Wallet> findById(UUID walletId) {
        return jpaWalletRepository.findById(walletId).map(WalletEntity::toDomain);
    }

    @Transactional
    @Override
    public void recordTransaction(Transaction transaction) {
        TransactionEntity entity = TransactionEntity.fromDomain(transaction);
        jpaTransactionRepository.save(entity);
    }

    @Override
    public BigDecimal getHistoricalBalance(UUID walletId, LocalDateTime dateTime) {
        List<TransactionEntity> transactions = jpaTransactionRepository.findByWalletIdAndTimestampBefore(walletId, dateTime);
        BigDecimal balance = BigDecimal.ZERO;

        for (TransactionEntity tx : transactions) {
            switch (tx.getType()) {
                case DEPOSIT:
                case TRANSFER_RECEIVED:
                    balance = balance.add(tx.getAmount());
                    break;
                case WITHDRAWAL:
                case TRANSFER_SENT:
                    balance = balance.subtract(tx.getAmount());
                    break;
            }
        }
        return balance;
    }

    @Override
    public boolean hasProcessed(UUID idempotencyKey) {
        return jpaTransactionRepository.existsByIdempotencyKey(idempotencyKey);
    }
}