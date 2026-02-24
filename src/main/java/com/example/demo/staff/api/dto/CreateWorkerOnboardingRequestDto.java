package com.example.demo.staff.api.dto;

import com.example.demo.auth.domain.Role;
import com.example.demo.common.annotation.PhoneNumber;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateWorkerOnboardingRequestDto(

        @NotBlank @Email
        String email,

        @NotBlank
        String password,

        @NotNull
        Role role,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        @PhoneNumber
        String phoneNumber,

        @Past @NotNull
        LocalDate birthDate
) {}
