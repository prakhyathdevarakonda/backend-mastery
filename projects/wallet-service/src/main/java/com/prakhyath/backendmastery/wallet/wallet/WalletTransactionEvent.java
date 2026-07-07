package com.prakhyath.backendmastery.wallet.wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletTransactionEvent(
        String eventId,
        Long walletId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant occurredAt
) {
    static WalletTransactionEvent from(WalletTransaction tx) {
        return new WalletTransactionEvent(
                UUID.randomUUID().toString(),
                tx.getWalletId(),
                tx.getType(),
                tx.getAmount(),
                tx.getBalanceAfter(),
                tx.getCreatedAt()
        );
    }
}
