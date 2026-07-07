package com.prakhyath.backendmastery.wallet.wallet;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallet_transactions")
class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private Instant createdAt;

    protected WalletTransaction() {
    }

    WalletTransaction(Wallet wallet, TransactionType type, BigDecimal amount) {
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = wallet.getBalance();
        this.createdAt = Instant.now();
    }

    Long getId() {
        return id;
    }

    Long getWalletId() {
        return wallet.getId();
    }

    TransactionType getType() {
        return type;
    }

    BigDecimal getAmount() {
        return amount;
    }

    BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    Instant getCreatedAt() {
        return createdAt;
    }
}
