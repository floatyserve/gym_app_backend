package com.example.demo.card.service.model;

import com.example.demo.card.domain.AccessCardStatus;

public record AccessCardSearchCriteria(
        String code,
        AccessCardStatus status,
        Long customerId
) {
}
