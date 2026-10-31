package com.prakhyath.backendmastery.screening.workflow;

import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/screenings")
public class ScreeningController {

    private final ScreeningStore store;
    private final ScreeningEventPublisher publisher;

    public ScreeningController(ScreeningStore store, ScreeningEventPublisher publisher) {
        this.store = store;
        this.publisher = publisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ScreeningSummary create(@Valid @RequestBody CreateScreeningRequest request) {
        String screeningId = UUID.randomUUID().toString();
        String correlationId = readCorrelationId();

        ScreeningSummary summary = store.create(screeningId);
        publisher.publishRequest(new ScreeningRequestedEvent(
                UUID.randomUUID().toString(),
                screeningId,
                request.candidateName(),
                request.email(),
                request.resumeText(),
                Instant.now(),
                correlationId
        ));

        return summary;
    }

    @GetMapping("/{screeningId}")
    public ScreeningSummary get(@PathVariable String screeningId) {
        return store.get(screeningId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found"));
    }

    @PostMapping("/{screeningId}/decision")
    public Map<String, String> submitDecision(@PathVariable String screeningId, @Valid @RequestBody DecisionRequest request) {
        ScreeningSummary summary = store.get(screeningId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found"));

        if (summary.status() != ScreeningStatus.HUMAN_REVIEW_REQUIRED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only HUMAN_REVIEW_REQUIRED can receive decisions");
        }

        publisher.publishDecision(new ScreeningDecisionEvent(
                UUID.randomUUID().toString(),
                screeningId,
                request.decision(),
                request.reason(),
                Instant.now(),
                readCorrelationId()
        ));

        return Map.of("message", "Decision accepted", "screeningId", screeningId);
    }

    private String readCorrelationId() {
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        return correlationId == null ? UUID.randomUUID().toString() : correlationId;
    }
}
