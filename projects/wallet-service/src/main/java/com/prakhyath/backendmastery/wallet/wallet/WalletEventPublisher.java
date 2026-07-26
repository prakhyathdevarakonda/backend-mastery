package com.prakhyath.backendmastery.wallet.wallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
class WalletEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(WalletEventPublisher.class);
    private static final String TOPIC = "wallet.transactions";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    WalletEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onWalletTransactionCommitted(WalletTransactionCommittedEvent event) {
        WalletTransactionEvent payload = event.payload();
        kafkaTemplate.send(TOPIC, payload.walletId().toString(), payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error(
                                "Failed to publish wallet transaction eventId={} walletId={}",
                                payload.eventId(),
                                payload.walletId(),
                                ex
                        );
                        return;
                    }
                    log.info(
                            "Published wallet transaction eventId={} walletId={} type={} topic={}",
                            payload.eventId(),
                            payload.walletId(),
                            payload.type(),
                            TOPIC
                    );
                });
    }
}
