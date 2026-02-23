package com.example.demo.staff.api.dto;

public record SimpleWorkerResponseDto(
        Long id,
        String fullName,
        String email,
        String role
) {
}
