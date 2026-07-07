package com.prakhyath.backendmastery.ledger;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletTransactionEvent(
        String eventId,
        Long walletId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant occurredAt
) {
}
