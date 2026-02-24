package com.example.demo.staff.api.dto;

import com.example.demo.auth.domain.Role;
import com.example.demo.common.annotation.PhoneNumber;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateWorkerRequest(
        @NotBlank
        @Email
        String email,
        @NotNull
        Role role,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        @PhoneNumber
        String phoneNumber,
        @NotNull @Past
        LocalDate birthDate
) {
}
