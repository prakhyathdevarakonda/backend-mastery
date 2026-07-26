package com.prakhyath.backendmastery.ledger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
class WalletTransactionEventListener {
    private static final Logger log = LoggerFactory.getLogger(WalletTransactionEventListener.class);
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "wallet.transactions", groupId = "${spring.kafka.consumer.group-id:ledger-service}")
    void onWalletTransaction(WalletTransactionEvent event) {
        if (!processedEventIds.add(event.eventId())) {
            log.info("Duplicate event ignored eventId={} walletId={}", event.eventId(), event.walletId());
            return;
        }
        log.info(
                "Ledger consumed eventId={} walletId={} type={} amount={} balanceAfter={}",
                event.eventId(),
                event.walletId(),
                event.type(),
                event.amount(),
                event.balanceAfter()
        );
    }

    boolean hasProcessed(String eventId) {
        return processedEventIds.contains(eventId);
    }
}
