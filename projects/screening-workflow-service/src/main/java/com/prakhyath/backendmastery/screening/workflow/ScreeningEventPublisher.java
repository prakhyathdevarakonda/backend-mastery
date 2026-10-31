package com.prakhyath.backendmastery.screening.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ScreeningEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ScreeningEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ScreeningEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRequest(ScreeningRequestedEvent event) {
        kafkaTemplate.send(ScreeningTopics.REQUESTS, event.screeningId(), event);
        log.info("Published screening request eventId={} screeningId={}", event.eventId(), event.screeningId());
    }

    public void publishResult(ScreeningResultEvent event) {
        kafkaTemplate.send(ScreeningTopics.RESULTS, event.screeningId(), event);
        log.info("Published screening result eventId={} screeningId={} status={}", event.eventId(), event.screeningId(), event.status());
    }

    public void publishDecision(ScreeningDecisionEvent event) {
        kafkaTemplate.send(ScreeningTopics.DECISIONS, event.screeningId(), event);
        log.info("Published screening decision eventId={} screeningId={} decision={}", event.eventId(), event.screeningId(), event.decision());
    }
}
