package com.prakhyath.backendmastery.screening.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScreeningDecisionListener {

    private static final Logger log = LoggerFactory.getLogger(ScreeningDecisionListener.class);

    private final ScreeningEventPublisher publisher;
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public ScreeningDecisionListener(ScreeningEventPublisher publisher) {
        this.publisher = publisher;
    }

    @KafkaListener(
            topics = ScreeningTopics.DECISIONS,
            groupId = "screening-human-review",
            properties = {
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.prakhyath.backendmastery.screening.workflow.ScreeningDecisionEvent"
            }
    )
    public void consume(ScreeningDecisionEvent event) {
        if (!processedEventIds.add(event.eventId())) {
            log.info("Duplicate decision ignored eventId={}", event.eventId());
            return;
        }

        ScreeningStatus status = event.decision() == ScreeningDecision.APPROVE
                ? ScreeningStatus.APPROVED
                : ScreeningStatus.REJECTED;

        publisher.publishResult(new ScreeningResultEvent(
                UUID.randomUUID().toString(),
                event.screeningId(),
                status,
                null,
                "Human decision: " + event.decision() + (event.reason() == null ? "" : " - " + event.reason()),
                Instant.now(),
                event.correlationId()
        ));

        log.info("Human decision applied screeningId={} decision={}", event.screeningId(), event.decision());
    }
}
