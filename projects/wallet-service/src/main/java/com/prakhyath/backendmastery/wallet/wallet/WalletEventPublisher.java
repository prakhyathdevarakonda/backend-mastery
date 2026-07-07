package com.prakhyath.backendmastery.wallet.wallet;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
class WalletEventPublisher {
    private static final String TOPIC = "wallet.transactions";

    private final KafkaTemplate<String, WalletTransactionEvent> kafkaTemplate;

    WalletEventPublisher(KafkaTemplate<String, WalletTransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onWalletTransactionCommitted(WalletTransactionCommittedEvent event) {
        WalletTransactionEvent payload = event.payload();
        kafkaTemplate.send(TOPIC, payload.walletId().toString(), payload);
    }
}
