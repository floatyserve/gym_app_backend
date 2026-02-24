package com.example.demo.staff.service.impl;

import com.example.demo.auth.domain.User;
import com.example.demo.auth.service.UserService;
import com.example.demo.common.ResourceType;
import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.staff.command.UpdateWorkerCommand;
import com.example.demo.staff.domain.Worker;
import com.example.demo.staff.repository.WorkerRepository;
import com.example.demo.staff.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkerServiceJpa implements WorkerService {

    private final WorkerRepository workerRepository;
    private final UserService userService;

    @Override
    public Worker findById(Long id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.WORKER,
                        "id"
                ));
    }

    @Override
    public Worker findByUserId(Long userId) {
        return workerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ReferenceNotFoundException(
                                ResourceType.WORKER,
                                "userId"
                        ));
    }

    @Override
    public Page<Worker> findAll(Pageable pageable) {
        return workerRepository.findAll(pageable);
    }

    @Override
    public Worker create(String firstName,
                         String lastName,
                         String phoneNumber,
                         LocalDate birthDate,
                         Long userId) {
        if (workerRepository.existsByUserId(userId)) {
            throw new IllegalStateException("User already assigned to a worker");
        }

        User assignedUser = userService.findById(userId);

        Worker newWorker = new Worker(
                firstName,
                lastName,
                phoneNumber,
                birthDate,
                Instant.now(),
                assignedUser
        );

        return workerRepository.save(newWorker);
    }

    @Override
    public Worker update(Long id, UpdateWorkerCommand command) {
        Worker worker = findById(id);
        User user = worker.getUser();

        if (!user.getEmail().equals(command.email())) {
            userService.assertEmailAvailable(command.email(), user.getId());
        }

        worker.updatePersonalInfo(
                command.firstName(),
                command.lastName(),
                command.phoneNumber(),
                command.birthDate()
        );

        user.updateAccount(
                command.email(),
                command.role()
        );

        return worker;
    }
}
