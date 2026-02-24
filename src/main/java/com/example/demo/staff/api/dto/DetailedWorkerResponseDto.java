package com.example.demo.staff.api.dto;

import java.time.LocalDate;

public record DetailedWorkerResponseDto(
        Long id,
        String email,
        String role,
        boolean active,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,
        LocalDate hiredAt,
        Long userId
) {
}
