package com.prakhyath.backendmastery.wallet.wallet;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findWithLockById(Long id);
}
