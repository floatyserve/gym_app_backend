package com.example.demo.card.api.dto;

import com.example.demo.card.domain.AccessCardStatus;

public record AccessCardSearchRequest(
        String code,
        AccessCardStatus status,
        Long customerId
) {
}
