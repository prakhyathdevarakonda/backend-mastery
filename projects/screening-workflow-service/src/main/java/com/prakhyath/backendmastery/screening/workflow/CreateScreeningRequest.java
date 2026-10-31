package com.prakhyath.backendmastery.screening.workflow;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateScreeningRequest(
        @NotBlank String candidateName,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 30, max = 5000) String resumeText
) {
}
