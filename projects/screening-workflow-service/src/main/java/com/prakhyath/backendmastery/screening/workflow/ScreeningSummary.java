package com.prakhyath.backendmastery.screening.workflow;

import java.time.Instant;

public record ScreeningSummary(
        String screeningId,
        ScreeningStatus status,
        Integer score,
        String reason,
        Instant updatedAt
) {
}
