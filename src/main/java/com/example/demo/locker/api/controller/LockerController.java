package com.example.demo.locker.api.controller;

import com.example.demo.common.api.dto.PageResponseDto;
import com.example.demo.locker.api.dto.CreateLockerRequest;
import com.example.demo.locker.api.dto.LockerResponseDto;
import com.example.demo.locker.api.dto.LockerSearchRequest;
import com.example.demo.locker.domain.Locker;
import com.example.demo.locker.domain.LockerStats;
import com.example.demo.locker.mapper.LockerMapper;
import com.example.demo.locker.service.LockerAssignmentService;
import com.example.demo.locker.service.LockerService;
import com.example.demo.locker.service.model.LockerSearchCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lockers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
public class LockerController {
    private final LockerService lockerService;
    private final LockerAssignmentService lockerAssignmentService;
    private final LockerMapper mapper;

    @GetMapping
    public PageResponseDto<LockerResponseDto> search(
            @ModelAttribute LockerSearchRequest request,
            Pageable pageable
    ) {
        LockerSearchCriteria criteria = new LockerSearchCriteria(
                request.number(),
                request.status(),
                request.occupied()
        );

        Page<Locker> lockers = lockerService.search(criteria, pageable);

        var dtos = lockers.map(locker -> mapper.toDto(
                        locker,
                        lockerAssignmentService.isLockerOccupied(locker.getId())
                )
        );

        return PageResponseDto.from(dtos);
    }

    @GetMapping("/stats")
    public LockerStats getLockerStats() {
        return lockerService.getLockerStats();
    }

    @PostMapping
    public LockerResponseDto createLocker(@RequestBody @Valid CreateLockerRequest request) {
        Locker locker = lockerService.create(request.number());

        return mapper.toDto(locker, lockerAssignmentService.isLockerOccupied(locker.getId()));
    }

    @PostMapping(value = "/{lockerId}/out-of-order")
    public LockerResponseDto markOutOfOrder(@PathVariable Long lockerId) {
        Locker locker = lockerService.findById(lockerId);

        Locker updatedLocker = lockerService.outOfOrder(locker);

        return mapper.toDto(updatedLocker, lockerAssignmentService.isLockerOccupied(lockerId));

    }

    @PostMapping(value = "/{lockerId}/restore")
    public LockerResponseDto makeAvailable(@PathVariable Long lockerId) {
        Locker locker = lockerService.findById(lockerId);

        Locker updatedLocker = lockerService.restore(locker);

        return mapper.toDto(updatedLocker, lockerAssignmentService.isLockerOccupied(lockerId));
    }

}
