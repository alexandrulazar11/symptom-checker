package com.example.symptomchecker.controller.responses;

public record StartAssessmentResponse(
        String assessmentId,
        String nextQuestionId
) {}
