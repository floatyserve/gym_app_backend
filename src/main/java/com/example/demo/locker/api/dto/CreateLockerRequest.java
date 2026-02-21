package com.example.demo.locker.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateLockerRequest(
        @NotNull @Positive
        Integer number
) {}
