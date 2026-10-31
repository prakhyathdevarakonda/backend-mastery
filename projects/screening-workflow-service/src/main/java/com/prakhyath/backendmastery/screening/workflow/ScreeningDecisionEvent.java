package com.prakhyath.backendmastery.screening.workflow;

import java.time.Instant;

public record ScreeningDecisionEvent(
        String eventId,
        String screeningId,
        ScreeningDecision decision,
        String reason,
        Instant occurredAt,
        String correlationId
) {
}
