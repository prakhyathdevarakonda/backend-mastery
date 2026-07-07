package com.prakhyath.backendmastery.wallet.wallet;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletTransactionResponse(
        Long id,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant createdAt
) {

    static WalletTransactionResponse from(WalletTransaction transaction) {
        return new WalletTransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getCreatedAt()
        );
    }
}
