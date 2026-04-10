package com.example.demo.visit.service.model;

import java.time.Instant;

public record VisitSearchCriteria(
    String customerEmail,
    String receptionistEmail,
    Boolean active,
    Instant checkedInBefore,
    Instant checkedInAfter,
    Instant checkedOutBefore,
    Instant checkedOutAfter,
    Integer lockerNumber
) {
}