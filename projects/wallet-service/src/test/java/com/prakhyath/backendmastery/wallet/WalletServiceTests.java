package com.prakhyath.backendmastery.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.prakhyath.backendmastery.wallet.wallet.CreateWalletRequest;
import com.prakhyath.backendmastery.wallet.wallet.MoneyRequest;
import com.prakhyath.backendmastery.wallet.wallet.WalletResponse;
import com.prakhyath.backendmastery.wallet.wallet.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WalletServiceTests {

    @Autowired
    private WalletService walletService;

    @Test
    void depositAndWithdrawUpdateBalanceAndRecordTransactions() {
        WalletResponse created = walletService.createWallet(new CreateWalletRequest("learner"));

        WalletResponse afterDeposit = walletService.deposit(created.id(), new MoneyRequest(new BigDecimal("100.00")));
        WalletResponse afterWithdraw = walletService.withdraw(created.id(), new MoneyRequest(new BigDecimal("30.00")));

        assertThat(afterDeposit.balance()).isEqualByComparingTo("100.00");
        assertThat(afterWithdraw.balance()).isEqualByComparingTo("70.00");
        assertThat(walletService.listTransactions(created.id())).hasSize(2);
    }
}
