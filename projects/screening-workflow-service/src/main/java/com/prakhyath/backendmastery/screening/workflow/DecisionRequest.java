package com.prakhyath.backendmastery.screening.workflow;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DecisionRequest(
        @NotNull ScreeningDecision decision,
        @Size(max = 300) String reason
) {
}
