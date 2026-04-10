package com.example.demo.visit.api.controller;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.card.service.AccessCardService;
import com.example.demo.common.api.dto.PageResponseDto;
import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.service.CustomerService;
import com.example.demo.security.UserPrincipal;
import com.example.demo.staff.domain.Worker;
import com.example.demo.staff.service.WorkerService;
import com.example.demo.visit.api.dto.ActiveVisitResponseDto;
import com.example.demo.visit.api.dto.CheckInByEmailRequest;
import com.example.demo.visit.api.dto.CheckInByAccessCard;
import com.example.demo.visit.api.dto.VisitResponseDto;
import com.example.demo.visit.domain.Visit;
import com.example.demo.visit.mapper.VisitMapper;
import com.example.demo.visit.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.Instant;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
public class VisitController {
    private final VisitService visitService;
    private final VisitMapper mapper;
    private final AccessCardService accessCardService;
    private final Clock clock;
    private final WorkerService workerService;
    private final CustomerService customerService;

    @GetMapping
    public PageResponseDto<VisitResponseDto> getVisits(
            @RequestParam Instant from,
            @RequestParam Instant to,
            Pageable pageable
    ) {
        Page<Visit> visits = visitService.findVisits(from, to, pageable);
        return PageResponseDto.from(visits.map(mapper::toDto));
    }

    @GetMapping("/active")
    public PageResponseDto<ActiveVisitResponseDto> getAllActiveVisits(
            @PageableDefault(sort = "checkedInAt", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return PageResponseDto.from(
                visitService.findActiveVisitViews(pageable)
                        .map(mapper::toActiveDto)
        );
    }

    @PostMapping("/check-in")
    public VisitResponseDto checkIn(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CheckInByAccessCard request
    ) {
        AccessCard accessCard = accessCardService.findByCode(request.accessCardCode());

        Worker worker = workerService.findByUserId(principal.getId());

        Visit visit = visitService.checkInByAccessCard(accessCard, worker, clock.instant());

        return mapper.toDto(visit);
    }

    @PostMapping("/check-in/by-email")
    public VisitResponseDto checkInByEmail(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CheckInByEmailRequest request
            ) {
        Customer customer = customerService.findByEmail(request.customerEmail());

        Worker worker = workerService.findByUserId(principal.getId());

        Visit visit = visitService.checkInByCustomer(customer, worker, clock.instant());

        return mapper.toDto(visit);
    }

    @PostMapping("/{visitId}/check-out")
    public VisitResponseDto checkOut(
            @PathVariable Long visitId
    ) {
        Visit visit = visitService.checkOut(visitId, clock.instant());
        return mapper.toDto(visit);
    }
}
