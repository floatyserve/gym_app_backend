package com.example.demo.locker.service.model;

import com.example.demo.locker.domain.LockerStatus;

public record LockerSearchCriteria(
        Integer number,
        LockerStatus status,
        Boolean occupied
) {
}
