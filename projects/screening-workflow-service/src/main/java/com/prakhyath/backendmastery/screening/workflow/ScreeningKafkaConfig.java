package com.prakhyath.backendmastery.screening.workflow;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class ScreeningKafkaConfig {

    @Bean
    public NewTopic screeningRequestsTopic() {
        return new NewTopic(ScreeningTopics.REQUESTS, 1, (short) 1);
    }

    @Bean
    public NewTopic screeningResultsTopic() {
        return new NewTopic(ScreeningTopics.RESULTS, 1, (short) 1);
    }

    @Bean
    public NewTopic screeningDecisionsTopic() {
        return new NewTopic(ScreeningTopics.DECISIONS, 1, (short) 1);
    }
}
