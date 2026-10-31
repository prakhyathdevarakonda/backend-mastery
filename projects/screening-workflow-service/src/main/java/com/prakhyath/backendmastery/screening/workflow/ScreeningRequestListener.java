package com.prakhyath.backendmastery.screening.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScreeningRequestListener {

    private static final Logger log = LoggerFactory.getLogger(ScreeningRequestListener.class);

    private final ScreeningStore store;
    private final ScreeningEventPublisher publisher;
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public ScreeningRequestListener(ScreeningStore store, ScreeningEventPublisher publisher) {
        this.store = store;
        this.publisher = publisher;
    }

    @KafkaListener(
            topics = ScreeningTopics.REQUESTS,
            groupId = "screening-ai-worker",
            properties = {
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.prakhyath.backendmastery.screening.workflow.ScreeningRequestedEvent"
            }
    )
    public void consume(ScreeningRequestedEvent event) {
        if (!processedEventIds.add(event.eventId())) {
            log.info("Duplicate request ignored eventId={}", event.eventId());
            return;
        }

        store.upsert(event.screeningId(), ScreeningStatus.PROCESSING, null, "AI review in progress");

        int score = calculateScore(event.resumeText());
        ScreeningStatus status = score >= 70 ? ScreeningStatus.APPROVED
                : (score >= 45 ? ScreeningStatus.HUMAN_REVIEW_REQUIRED : ScreeningStatus.REJECTED);
        String reason = status == ScreeningStatus.HUMAN_REVIEW_REQUIRED
                ? "Borderline score; requires recruiter decision"
                : "Auto-evaluated by sample AI rules";

        publisher.publishResult(new ScreeningResultEvent(
                java.util.UUID.randomUUID().toString(),
                event.screeningId(),
                status,
                score,
                reason,
                Instant.now(),
                event.correlationId()
        ));
    }

    private int calculateScore(String resumeText) {
        String text = resumeText == null ? "" : resumeText.toLowerCase();
        int score = 30;
        if (text.contains("spring")) score += 20;
        if (text.contains("kafka")) score += 20;
        if (text.contains("microservice")) score += 15;
        if (text.contains("testing") || text.contains("junit")) score += 10;
        if (text.length() > 500) score += 10;
        return Math.min(score, 100);
    }
}
