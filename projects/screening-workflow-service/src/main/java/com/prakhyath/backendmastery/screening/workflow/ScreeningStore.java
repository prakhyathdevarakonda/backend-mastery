package com.prakhyath.backendmastery.screening.workflow;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScreeningStore {

    private final Map<String, ScreeningSummary> screenings = new ConcurrentHashMap<>();

    public ScreeningSummary create(String screeningId) {
        ScreeningSummary summary = new ScreeningSummary(screeningId, ScreeningStatus.RECEIVED, null, "Received request", Instant.now());
        screenings.put(screeningId, summary);
        return summary;
    }

    public Optional<ScreeningSummary> get(String screeningId) {
        return Optional.ofNullable(screenings.get(screeningId));
    }

    public ScreeningSummary upsert(String screeningId, ScreeningStatus status, Integer score, String reason) {
        ScreeningSummary summary = new ScreeningSummary(screeningId, status, score, reason, Instant.now());
        screenings.put(screeningId, summary);
        return summary;
    }
}
