package com.example.symptomchecker.controller.responses;

import java.util.Map;

public record AssessmentResultResponse(
        String condition,
        Map<String, Double> probabilities
) {}
