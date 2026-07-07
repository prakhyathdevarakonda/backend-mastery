package com.prakhyath.backendmastery.wallet.wallet;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record MoneyRequest(@NotNull @DecimalMin(value = "0.01") BigDecimal amount) {
}
