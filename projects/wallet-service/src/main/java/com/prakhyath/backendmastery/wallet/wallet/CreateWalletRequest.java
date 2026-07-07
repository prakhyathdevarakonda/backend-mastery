package com.prakhyath.backendmastery.wallet.wallet;

import jakarta.validation.constraints.NotBlank;

public record CreateWalletRequest(@NotBlank String ownerName) {
}
