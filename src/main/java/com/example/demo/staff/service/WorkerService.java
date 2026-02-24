package com.example.demo.staff.service;

import com.example.demo.staff.command.UpdateWorkerCommand;
import com.example.demo.staff.domain.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface WorkerService {
    Worker findById(Long id);
    Worker findByUserId(Long userId);
    Page<Worker> findAll(Pageable pageable);
    Worker create(
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate birthDate,
            Long userId
    );
    Worker update(Long id, UpdateWorkerCommand command);
}
