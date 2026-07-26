package com.prakhyath.backendmastery.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class WalletTransactionEventListenerTest {

    @Test
    void ignoresDuplicateEventId() {
        WalletTransactionEventListener listener = new WalletTransactionEventListener();
        WalletTransactionEvent event = sampleEvent("evt-dup-1");

        listener.onWalletTransaction(event);
        listener.onWalletTransaction(event);

        assertThat(listener.hasProcessed("evt-dup-1")).isTrue();
    }

    @Test
    void processesDistinctEventIds() {
        WalletTransactionEventListener listener = new WalletTransactionEventListener();

        listener.onWalletTransaction(sampleEvent("evt-1"));
        listener.onWalletTransaction(sampleEvent("evt-2"));

        assertThat(listener.hasProcessed("evt-1")).isTrue();
        assertThat(listener.hasProcessed("evt-2")).isTrue();
    }

    private static WalletTransactionEvent sampleEvent(String eventId) {
        return new WalletTransactionEvent(
                eventId,
                42L,
                TransactionType.DEPOSIT,
                new BigDecimal("10.00"),
                new BigDecimal("110.00"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}
