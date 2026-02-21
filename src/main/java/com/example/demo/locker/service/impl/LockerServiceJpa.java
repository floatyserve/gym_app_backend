package com.example.demo.locker.service.impl;

import com.example.demo.common.ResourceType;
import com.example.demo.exceptions.AlreadyExistsException;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.locker.domain.Locker;
import com.example.demo.locker.domain.LockerStats;
import com.example.demo.locker.domain.LockerStatus;
import com.example.demo.locker.repository.LockerRepository;
import com.example.demo.locker.repository.specification.LockerSpecification;
import com.example.demo.locker.service.LockerAssignmentService;
import com.example.demo.locker.service.LockerService;
import com.example.demo.locker.service.model.LockerSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LockerServiceJpa implements LockerService {
    private final LockerRepository lockerRepository;
    private final LockerAssignmentService lockerAssignmentService;

    @Override
    public Page<Locker> search(LockerSearchCriteria criteria, Pageable pageable) {
        Specification<Locker> specification = LockerSpecification.build(
                criteria.number(),
                criteria.status(),
                criteria.occupied()
        );

        return lockerRepository.findAll(specification,pageable);
    }

    @Override
    public Locker findById(Long id) {
        return lockerRepository.findById(id)
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.LOCKER,
                        "id"
                ));
    }

    @Override
    public Locker findByNumber(Integer number) {
        return lockerRepository.findByNumber(number)
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.LOCKER,
                        "number"
                ));
    }

    @Override
    public Locker findFirstAvailable() {
        Specification<Locker> spec = LockerSpecification.build(null, LockerStatus.AVAILABLE, false);
        PageRequest pageRequest = PageRequest.of(0, 1);

        return lockerRepository
                .findAll(spec, pageRequest)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.LOCKER,
                        null,
                        "No available lockers left"
                ));
    }

    @Override
    public Locker create(Integer number) {
        if (lockerRepository.existsByNumber(number)) {
            throw new AlreadyExistsException(ResourceType.LOCKER, "number");
        }

        Locker locker = new Locker(
                number,
                LockerStatus.AVAILABLE
        );

        return lockerRepository.save(locker);
    }

    @Override
    public void assertAvailable(Locker locker) {
        if (locker.getStatus() != LockerStatus.AVAILABLE) {
            throw new BadRequestException(
                    ResourceType.LOCKER,
                    "status",
                    "Locker is out of order."
            );
        }

        if (lockerAssignmentService.isLockerOccupied(locker.getId())) {
            throw new BadRequestException(
                    ResourceType.LOCKER,
                    null,
                    "Locker is currently occupied"
            );
        }
    }

    @Override
    public Locker makeUnavailable(Locker locker) {
        assertAvailable(locker);

        locker.markOutOfOrder();
        return locker;
    }

    @Override
    public LockerStats getLockerStats() {
        long totalCount = lockerRepository.count();
        long availableCount = lockerRepository.countAvailableLockers();
        long occupiedCount = lockerRepository.countOccupiedLockers();
        long outOfOrderCount = lockerRepository.countUnavailableLockers();

        return new LockerStats(totalCount, availableCount, occupiedCount, outOfOrderCount);
    }
}
