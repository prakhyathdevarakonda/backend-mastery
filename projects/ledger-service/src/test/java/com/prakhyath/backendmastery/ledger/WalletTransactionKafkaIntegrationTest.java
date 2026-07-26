package com.prakhyath.backendmastery.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("kafka-it")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@EmbeddedKafka(partitions = 1, topics = {"wallet.transactions"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.kafka.producer.properties.spring.json.add.type.headers=false",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=com.prakhyath.backendmastery.ledger",
        "spring.kafka.consumer.properties.spring.json.use.type.headers=false",
        "spring.kafka.consumer.properties.spring.json.value.default.type=com.prakhyath.backendmastery.ledger.WalletTransactionEvent",
        "app.kafka.enabled=true"
})
class WalletTransactionKafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private WalletTransactionEventListener listener;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void waitForKafkaListener() throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofSeconds(15).toNanos();
        while (System.nanoTime() < deadline) {
            boolean ready = kafkaListenerEndpointRegistry.getListenerContainers().stream()
                    .allMatch(WalletTransactionKafkaIntegrationTest::listenerReady);
            if (ready) {
                return;
            }
            Thread.sleep(100);
        }
        throw new IllegalStateException("Kafka listener did not start in time");
    }

    private static boolean listenerReady(MessageListenerContainer container) {
        if (!container.isRunning()) {
            return false;
        }
        if (container instanceof KafkaMessageListenerContainer<?, ?> kafkaContainer) {
            return !kafkaContainer.getAssignedPartitions().isEmpty();
        }
        return true;
    }

    @Test
    void consumesWalletTransactionEventFromKafka() throws Exception {
        String eventId = "evt-kafka-" + UUID.randomUUID();
        WalletTransactionEvent event = new WalletTransactionEvent(
                eventId,
                7L,
                TransactionType.WITHDRAW,
                new BigDecimal("15.00"),
                new BigDecimal("85.00"),
                Instant.parse("2026-07-26T12:00:00Z")
        );

        kafkaTemplate.send("wallet.transactions", event.walletId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        throw new IllegalStateException("Kafka publish failed", ex);
                    }
                })
                .get(5, TimeUnit.SECONDS);

        assertThat(awaitProcessed(eventId)).isTrue();
    }

    private boolean awaitProcessed(String eventId) throws InterruptedException {
        Duration timeout = Duration.ofSeconds(15);
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (listener.hasProcessed(eventId)) {
                return true;
            }
            Thread.sleep(100);
        }
        return listener.hasProcessed(eventId);
    }
}
