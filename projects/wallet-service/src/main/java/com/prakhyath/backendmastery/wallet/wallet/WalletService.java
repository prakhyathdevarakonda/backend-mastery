package com.prakhyath.backendmastery.wallet.wallet;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private final WalletRepository wallets;
    private final WalletTransactionRepository transactions;
    private final ApplicationEventPublisher eventPublisher;

    WalletService(
            WalletRepository wallets,
            WalletTransactionRepository transactions,
            ApplicationEventPublisher eventPublisher
    ) {
        this.wallets = wallets;
        this.transactions = transactions;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = wallets.save(new Wallet(request.ownerName()));
        return WalletResponse.from(wallet);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long id) {
        return WalletResponse.from(findWallet(id));
    }

    @Transactional
    public WalletResponse deposit(Long id, MoneyRequest request) {
        Wallet wallet = findWallet(id);
        BigDecimal amount = request.amount();
        requirePositive(amount);
        wallet.deposit(amount);
        WalletTransaction tx = transactions.save(new WalletTransaction(wallet, TransactionType.DEPOSIT, amount));
        publishAfterCommit(tx);
        return WalletResponse.from(wallet);
    }

    @Transactional
    public WalletResponse withdraw(Long id, MoneyRequest request) {
        Wallet wallet = findWallet(id);
        BigDecimal amount = request.amount();
        requirePositive(amount);
        wallet.withdraw(amount);
        WalletTransaction tx = transactions.save(new WalletTransaction(wallet, TransactionType.WITHDRAW, amount));
        publishAfterCommit(tx);
        return WalletResponse.from(wallet);
    }

    @Transactional
    public WalletResponse withdrawWithRowLock(Long id, MoneyRequest request) {
        Wallet wallet = wallets.findWithLockById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + id));
        BigDecimal amount = request.amount();
        requirePositive(amount);
        wallet.withdraw(amount);
        WalletTransaction tx = transactions.save(new WalletTransaction(wallet, TransactionType.WITHDRAW, amount));
        publishAfterCommit(tx);
        return WalletResponse.from(wallet);
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> listTransactions(Long walletId) {
        if (!wallets.existsById(walletId)) {
            throw new IllegalArgumentException("Wallet not found: " + walletId);
        }
        return transactions.findByWalletIdOrderByCreatedAtAsc(walletId)
                .stream()
                .map(WalletTransactionResponse::from)
                .toList();
    }

    private Wallet findWallet(Long id) {
        return wallets.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + id));
    }

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void publishAfterCommit(WalletTransaction tx) {
        WalletTransactionEvent event = WalletTransactionEvent.from(tx);
        eventPublisher.publishEvent(new WalletTransactionCommittedEvent(event));
    }
}
