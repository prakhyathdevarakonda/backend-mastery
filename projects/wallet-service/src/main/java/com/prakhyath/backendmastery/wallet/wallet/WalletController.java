package com.prakhyath.backendmastery.wallet.wallet;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
class WalletController {

    private final WalletService walletService;

    WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse wallet = walletService.createWallet(request);
        return ResponseEntity.created(URI.create("/api/wallets/" + wallet.id())).body(wallet);
    }

    @GetMapping("/{id}")
    WalletResponse getWallet(@PathVariable Long id) {
        return walletService.getWallet(id);
    }

    @PostMapping("/{id}/deposits")
    WalletResponse deposit(@PathVariable Long id, @Valid @RequestBody MoneyRequest request) {
        return walletService.deposit(id, request);
    }

    @PostMapping("/{id}/withdrawals")
    WalletResponse withdraw(@PathVariable Long id, @Valid @RequestBody MoneyRequest request) {
        return walletService.withdraw(id, request);
    }

    @PostMapping("/{id}/withdrawals/locked")
    WalletResponse withdrawWithLock(@PathVariable Long id, @Valid @RequestBody MoneyRequest request) {
        return walletService.withdrawWithRowLock(id, request);
    }

    @GetMapping("/{id}/transactions")
    List<WalletTransactionResponse> listTransactions(@PathVariable Long id) {
        return walletService.listTransactions(id);
    }
}
