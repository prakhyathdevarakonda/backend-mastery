package com.prakhyath.backendmastery.screening.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScreeningResultListener {

    private static final Logger log = LoggerFactory.getLogger(ScreeningResultListener.class);

    private final ScreeningStore store;
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public ScreeningResultListener(ScreeningStore store) {
        this.store = store;
    }

    @KafkaListener(
            topics = ScreeningTopics.RESULTS,
            groupId = "screening-result-updater",
            properties = {
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.prakhyath.backendmastery.screening.workflow.ScreeningResultEvent"
            }
    )
    public void consume(ScreeningResultEvent event) {
        if (!processedEventIds.add(event.eventId())) {
            log.info("Duplicate result ignored eventId={}", event.eventId());
            return;
        }

        store.upsert(event.screeningId(), event.status(), event.score(), event.reason());
        log.info("Screening updated screeningId={} status={} score={}", event.screeningId(), event.status(), event.score());
    }
}
