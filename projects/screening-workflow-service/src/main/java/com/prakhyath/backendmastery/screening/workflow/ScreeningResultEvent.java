package com.prakhyath.backendmastery.screening.workflow;

import java.time.Instant;

public record ScreeningResultEvent(
        String eventId,
        String screeningId,
        ScreeningStatus status,
        Integer score,
        String reason,
        Instant occurredAt,
        String correlationId
) {
}
