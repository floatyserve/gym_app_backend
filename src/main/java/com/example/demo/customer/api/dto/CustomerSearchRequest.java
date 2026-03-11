package com.example.demo.customer.api.dto;

public record CustomerSearchRequest(
        String fullName,
        String email,
        String phoneNumber,
        String cardCode
) {
}
