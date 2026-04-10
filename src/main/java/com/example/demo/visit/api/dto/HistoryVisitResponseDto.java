package com.example.demo.visit.api.dto;

import java.time.Instant;

public record HistoryVisitResponseDto (
    Long id,
    String customerFullName,
    String customerEmail,
    String receptionistFullName,
    String receptionistEmail,
    Instant checkedInAt,
    Instant checkedOutAt,
    Integer lockerNumber
) {}