package com.prakhyath.backendmastery.screening.workflow;

import java.time.Instant;

public record ScreeningRequestedEvent(
        String eventId,
        String screeningId,
        String candidateName,
        String email,
        String resumeText,
        Instant occurredAt,
        String correlationId
) {
}
