package com.prakhyath.backendmastery.wallet.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
class WalletConcurrencyTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PlatformTransactionManager txManager;

    @Test
    void optimisticLockingAllowsOnlyOneConflictingWithdraw() throws Exception {
        WalletResponse wallet = walletService.createWallet(new CreateWalletRequest("optimistic-user"));
        walletService.deposit(wallet.id(), new MoneyRequest(new BigDecimal("100.00")));

        CountDownLatch bothLoaded = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        List<Throwable> failures = new ArrayList<>();

        Callable<Void> task = () -> {
            TransactionTemplate tx = new TransactionTemplate(txManager);
            try {
                tx.executeWithoutResult(status -> {
                    Wallet managed = walletRepository.findById(wallet.id()).orElseThrow();
                    bothLoaded.countDown();
                    await(start);
                    managed.withdraw(new BigDecimal("60.00"));
                    walletRepository.saveAndFlush(managed);
                });
            } catch (Throwable ex) {
                failures.add(ex);
            }
            return null;
        };

        Future<Void> f1 = pool.submit(task);
        Future<Void> f2 = pool.submit(task);
        bothLoaded.await();
        start.countDown();
        f1.get();
        f2.get();
        pool.shutdownNow();

        WalletResponse finalWallet = walletService.getWallet(wallet.id());
        assertThat(finalWallet.balance()).isEqualByComparingTo("40.00");
        assertThat(failures).hasSize(1);
        assertThat(hasCause(failures.get(0), ObjectOptimisticLockingFailureException.class)).isTrue();
    }

    @Test
    void pessimisticLockingSerializesWithdrawals() throws Exception {
        WalletResponse wallet = walletService.createWallet(new CreateWalletRequest("pessimistic-user"));
        walletService.deposit(wallet.id(), new MoneyRequest(new BigDecimal("100.00")));

        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        List<Throwable> failures = new ArrayList<>();

        Callable<Void> task = () -> {
            try {
                await(start);
                walletService.withdrawWithRowLock(wallet.id(), new MoneyRequest(new BigDecimal("50.00")));
            } catch (Throwable ex) {
                failures.add(ex);
            }
            return null;
        };

        Future<Void> f1 = pool.submit(task);
        Future<Void> f2 = pool.submit(task);
        start.countDown();
        f1.get();
        f2.get();
        pool.shutdownNow();

        WalletResponse finalWallet = walletService.getWallet(wallet.id());
        assertThat(failures).isEmpty();
        assertThat(finalWallet.balance()).isEqualByComparingTo("0.00");
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static boolean hasCause(Throwable ex, Class<? extends Throwable> targetType) {
        Throwable current = ex;
        while (current != null) {
            if (targetType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
