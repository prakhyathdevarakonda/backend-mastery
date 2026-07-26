package com.prakhyath.backendmastery.wallet.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest(properties = {
        "app.kafka.enabled=true",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.kafka.producer.properties.spring.json.add.type.headers=false"
})
@EmbeddedKafka(partitions = 1, topics = {"wallet.transactions"})
class WalletKafkaIntegrationTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void depositPublishesWalletTransactionEventAfterCommit() {
        WalletResponse wallet = walletService.createWallet(new CreateWalletRequest("kafka-user"));

        try (Consumer<String, WalletTransactionEvent> consumer = createConsumer()) {
            consumer.poll(Duration.ofMillis(500));

            walletService.deposit(wallet.id(), new MoneyRequest(new BigDecimal("25.00")));

            ConsumerRecord<String, WalletTransactionEvent> record =
                    KafkaTestUtils.getSingleRecord(consumer, "wallet.transactions", Duration.ofSeconds(15));

            WalletTransactionEvent event = record.value();
            assertThat(event.walletId()).isEqualTo(wallet.id());
            assertThat(event.type()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(event.amount()).isEqualByComparingTo("25.00");
            assertThat(event.balanceAfter()).isEqualByComparingTo("25.00");
            assertThat(event.eventId()).isNotBlank();
            assertThat(event.occurredAt()).isNotNull();
            assertThat(record.key()).isEqualTo(wallet.id().toString());
        }
    }

    private Consumer<String, WalletTransactionEvent> createConsumer() {
        Map<String, Object> props = new HashMap<>(
                KafkaTestUtils.consumerProps("wallet-service-test", "true", embeddedKafkaBroker)
        );
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.prakhyath.backendmastery.wallet.wallet");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, WalletTransactionEvent.class.getName());

        Consumer<String, WalletTransactionEvent> consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(WalletTransactionEvent.class, false)
        ).createConsumer();
        consumer.subscribe(java.util.List.of("wallet.transactions"));
        return consumer;
    }
}
