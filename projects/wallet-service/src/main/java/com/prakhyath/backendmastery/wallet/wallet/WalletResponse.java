package com.prakhyath.backendmastery.wallet.wallet;

import java.math.BigDecimal;

public record WalletResponse(Long id, String ownerName, BigDecimal balance) {

    static WalletResponse from(Wallet wallet) {
        return new WalletResponse(wallet.getId(), wallet.getOwnerName(), wallet.getBalance());
    }
}
