package com.example.demo.visit.api.controller;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.card.service.AccessCardService;
import com.example.demo.common.api.dto.PageResponseDto;
import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.service.CustomerService;
import com.example.demo.security.UserPrincipal;
import com.example.demo.staff.domain.Worker;
import com.example.demo.staff.service.WorkerService;
import com.example.demo.visit.api.dto.*;
import com.example.demo.visit.mapper.VisitMapper;
import com.example.demo.visit.service.VisitService;
import com.example.demo.visit.service.model.VisitSearchCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;

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
    public PageResponseDto<HistoryVisitResponseDto> search(
            @ModelAttribute VisitSearchRequest request,
            Pageable pageable
    ) {
        VisitSearchCriteria criteria = new VisitSearchCriteria(
                request.customerEmail(),
                request.receptionistEmail(),
                request.active(),
                request.checkedInBefore(),
                request.checkedInAfter(),
                request.checkedOutBefore(),
                request.checkedOutAfter(),
                request.lockerNumber()
        );

        return PageResponseDto.from(
                visitService.search(criteria, pageable)
                        .map(mapper::toHistoryDto)
        );
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
    public void checkIn(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CheckInByAccessCard request
    ) {
        AccessCard accessCard = accessCardService.findByCode(request.accessCardCode());

        Worker worker = workerService.findByUserId(principal.getId());

        visitService.checkInByAccessCard(accessCard, worker, clock.instant());
    }

    @PostMapping("/check-in/by-email")
    public void checkInByEmail(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CheckInByEmailRequest request
            ) {
        Customer customer = customerService.findByEmail(request.customerEmail());

        Worker worker = workerService.findByUserId(principal.getId());

        visitService.checkInByCustomer(customer, worker, clock.instant());
    }

    @PostMapping("/{visitId}/check-out")
    public void checkOut(
            @PathVariable Long visitId
    ) {
        visitService.checkOut(visitId, clock.instant());
    }
}
