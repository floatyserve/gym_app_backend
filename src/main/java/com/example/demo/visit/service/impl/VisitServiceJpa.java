package com.example.demo.visit.service.impl;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.common.ResourceType;
import com.example.demo.customer.domain.Customer;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.locker.domain.LockerAssignment;
import com.example.demo.locker.service.LockerAssignmentService;
import com.example.demo.membership.domain.Membership;
import com.example.demo.membership.service.MembershipLifecycleService;
import com.example.demo.membership.service.MembershipUsageService;
import com.example.demo.staff.domain.Worker;
import com.example.demo.visit.domain.ActiveVisitView;
import com.example.demo.visit.domain.Visit;
import com.example.demo.visit.repository.VisitRepository;
import com.example.demo.visit.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VisitServiceJpa implements VisitService {
    private final VisitRepository visitRepository;
    private final MembershipLifecycleService membershipLifecycleService;
    private final MembershipUsageService membershipUsageService;
    private final LockerAssignmentService lockerAssignmentService;

    @Override
    public Visit findById(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new ReferenceNotFoundException(ResourceType.VISIT, "id"));
    }

    @Override
    public Visit findActiveVisit(Long id) {
        Visit visit = findById(id);

        if (visit.getCheckedOutAt() != null) {
            throw new ReferenceNotFoundException(
                    ResourceType.VISIT,
                    "checkedOutAt",
                    "Requested visit is already checked out"
            );
        }

        return visit;
    }

    @Override
    public Page<ActiveVisitView> findActiveVisitViews(Pageable pageable) {
        return visitRepository.findActiveVisitViews(pageable);
    }

    @Override
    public Visit checkInByAccessCard(AccessCard accessCard, Worker worker, Instant at) {
        if (!accessCard.isActive()){
            throw new BadRequestException(
                    ResourceType.ACCESS_CARD,
                    "status",
                    "Card is not active, status:" + accessCard.getStatus()
            );
        }

        Customer customer = accessCard.getCustomer();

        return checkInByCustomer(customer, worker, at);
    }

    @Override
    public Visit checkInByCustomer(Customer customer, Worker worker, Instant at) {
        Optional<Membership> activeMembershipOptional = membershipLifecycleService.findValidActiveMembership(customer, at);

        Membership membership;

        membership = activeMembershipOptional
                .orElseGet(() -> membershipLifecycleService.activateNextPendingMembership(customer));

        if (membershipUsageService.isExhausted(membership, at)) {
            throw new BadRequestException(
                    ResourceType.MEMBERSHIP,
                    "visitLimit",
                    "Visit limit reached"
            );
        }

        Visit visit = visitRepository.save(new Visit(customer, worker, at));

        lockerAssignmentService.assignAvailableLockerToVisit(visit, at);

        return visit;
    }

    @Override
    public Visit checkOut(Long visitId, Instant at) {
        Visit visit = findActiveVisit(visitId);
        visit.checkout(at);

        LockerAssignment lockerAssignment = lockerAssignmentService.findActiveAssignmentForVisit(visit);
        lockerAssignment.release(at);

        return visit;
    }

    @Override
    public Page<Visit> findVisits(Instant from, Instant to, Pageable pageable) {
        if (from.isAfter(to)) {
            throw new BadRequestException(
                    ResourceType.VISIT,
                    "from",
                    "FROM date must be before TO date"
            );
        }

        return visitRepository.findByCheckedInAtIsBetween(from, to, pageable);
    }
}
