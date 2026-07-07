package com.prakhyath.backendmastery.wallet.wallet;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "wallets")
class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private Instant createdAt;

    @Version
    private Long version;

    protected Wallet() {
    }

    Wallet(String ownerName) {
        this.ownerName = ownerName;
        this.balance = BigDecimal.ZERO;
        this.createdAt = Instant.now();
    }

    Long getId() {
        return id;
    }

    String getOwnerName() {
        return ownerName;
    }

    BigDecimal getBalance() {
        return balance;
    }

    void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        balance = balance.subtract(amount);
    }
}
