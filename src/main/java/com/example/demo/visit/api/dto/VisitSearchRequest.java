package com.example.demo.visit.api.dto;

import java.time.Instant;

public record VisitSearchRequest(
    String customerEmail,
    String receptionistEmail,
    Boolean active,
    Instant checkedInBefore,
    Instant checkedInAfter,
    Instant checkedOutBefore,
    Instant checkedOutAfter,
    Integer lockerNumber
) {}