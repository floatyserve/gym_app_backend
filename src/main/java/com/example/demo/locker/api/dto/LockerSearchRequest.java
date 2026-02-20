package com.example.demo.locker.api.dto;

import com.example.demo.locker.domain.LockerStatus;

public record LockerSearchRequest(
        Integer number,
        LockerStatus status,
        Boolean occupied
) {
}
