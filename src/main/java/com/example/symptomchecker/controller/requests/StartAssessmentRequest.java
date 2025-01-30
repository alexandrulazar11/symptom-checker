package com.example.symptomchecker.controller.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record StartAssessmentRequest(
    @NotBlank String userId,
    @NotEmpty List<String> initialSymptoms
) { }
