package com.example.demo.staff.command;

import com.example.demo.auth.domain.Role;

import java.time.LocalDate;

public record UpdateWorkerCommand(
        String email,
        Role role,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate
) {
}
