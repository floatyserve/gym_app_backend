package com.example.demo.customer.service.model;

public record CustomerSearchCriteria(
        String fullName,
        String email,
        String phoneNumber,
        String cardCode
) {
}
