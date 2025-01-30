package com.example.symptomchecker.controller.requests;

import jakarta.validation.constraints.NotBlank;

public record AnswerQuestionRequest(
    @NotBlank String questionId,
    @NotBlank String userResponse
) { }
