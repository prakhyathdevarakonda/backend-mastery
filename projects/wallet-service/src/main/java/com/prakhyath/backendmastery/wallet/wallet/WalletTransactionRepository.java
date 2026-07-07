package com.prakhyath.backendmastery.wallet.wallet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletIdOrderByCreatedAtAsc(Long walletId);
}
