package com.example.demo.visit.api.dto;

import jakarta.validation.constraints.NotNull;

public record FrontDeskCheckInRequest(
   @NotNull Long customerId
) {}
