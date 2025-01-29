package com.example.symptomchecker.controller.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull Integer age,
        @NotBlank String gender
) {}
