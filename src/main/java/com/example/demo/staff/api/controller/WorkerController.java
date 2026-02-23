package com.example.demo.staff.api.controller;

import com.example.demo.common.api.dto.PageResponseDto;
import com.example.demo.security.UserPrincipal;
import com.example.demo.staff.api.dto.CreateWorkerOnboardingRequestDto;
import com.example.demo.staff.api.dto.DetailedWorkerResponseDto;
import com.example.demo.staff.api.dto.SimpleWorkerResponseDto;
import com.example.demo.staff.domain.Worker;
import com.example.demo.staff.mapper.WorkerMapper;
import com.example.demo.staff.service.WorkerOnboardService;
import com.example.demo.staff.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;
    private final WorkerMapper workerMapper;
    private final WorkerOnboardService workerOnboardingService;
    private final Clock clock;

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping()
    public PageResponseDto<SimpleWorkerResponseDto> getAll(Pageable pageable){
        return PageResponseDto.from(
                workerService.findAll(pageable)
                    .map(workerMapper::toSimpleDto)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/{id}")
    public SimpleWorkerResponseDto getById(@PathVariable Long id){
        return workerMapper.toSimpleDto(workerService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/by-user-id/{userId}")
    public SimpleWorkerResponseDto getByUserId(@PathVariable Long userId){
        return workerMapper.toSimpleDto(workerService.findByUserId(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SimpleWorkerResponseDto createWorker(
            @Valid @RequestBody CreateWorkerOnboardingRequestDto req
    ) {
        Worker worker = workerOnboardingService.onboard(
                req.email(),
                req.password(),
                req.role(),
                req.firstName(),
                req.lastName(),
                req.phoneNumber(),
                req.birthDate(),
                clock.instant()
        );

        return workerMapper.toSimpleDto(worker);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/me")
    public DetailedWorkerResponseDto me(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Worker worker = workerService.findByUserId(principal.getId());
        return workerMapper.toDetailedDto(worker);
    }

}
