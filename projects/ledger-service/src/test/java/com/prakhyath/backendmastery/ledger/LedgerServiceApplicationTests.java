package com.prakhyath.backendmastery.ledger;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class LedgerServiceApplicationTests {

    @Test
    void applicationClassLoads() {
        assertThatCode(() -> Class.forName(LedgerServiceApplication.class.getName()))
                .doesNotThrowAnyException();
    }
}
