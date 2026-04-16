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
import com.example.demo.visit.domain.HistoryVisitView;
import com.example.demo.visit.domain.Visit;
import com.example.demo.visit.repository.VisitRepository;
import com.example.demo.visit.service.VisitService;
import com.example.demo.visit.service.model.VisitSearchCriteria;
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
    public void checkInByAccessCard(AccessCard accessCard, Worker worker, Instant at) {
        if (!accessCard.isActive()){
            throw new BadRequestException(
                    ResourceType.ACCESS_CARD,
                    "status",
                    "Card is not active, status:" + accessCard.getStatus()
            );
        }

        Customer customer = accessCard.getCustomer();

        checkInByCustomer(customer, worker, at);
    }

    @Override
    public void checkInByCustomer(Customer customer, Worker worker, Instant at) {
        if (isCustomerCheckedIn(customer)) {
            throw new BadRequestException(
                    ResourceType.VISIT, "customer", "Customer is already checked in"
            );
        }

        resolveMembershipForCheckIn(customer, at);

        Visit visit = visitRepository.save(new Visit(customer, worker, at));
        lockerAssignmentService.assignAvailableLockerToVisit(visit, at);
    }

    private void resolveMembershipForCheckIn(Customer customer, Instant at) {
        Optional<Membership> activeOptional = membershipLifecycleService.findValidActiveMembership(customer, at);

        if (activeOptional.isPresent()) {
            Membership activeMembership = activeOptional.get();

            if (!membershipUsageService.isExhausted(activeMembership, at)) {
                return;
            }

            membershipLifecycleService.finishMembership(activeMembership, at);
        }

        Membership newMembership = membershipLifecycleService.activateNextPendingMembership(customer, at);

        if (membershipUsageService.isExhausted(newMembership, at)) {
            throw new BadRequestException(
                    ResourceType.MEMBERSHIP,
                    "visitLimit",
                    "Visit limit reached"
            );
        }
    }

    private boolean isCustomerCheckedIn(Customer customer) {
        return visitRepository.existsByCustomerAndCheckedOutAtIsNull(customer);
    }

    @Override
    public void checkOut(Long visitId, Instant at) {
        Visit visit = findActiveVisit(visitId);
        visit.checkout(at);

        LockerAssignment lockerAssignment = lockerAssignmentService.findActiveAssignmentForVisit(visit);
        lockerAssignment.release(at);

    }

    @Override
    public Page<HistoryVisitView> search(VisitSearchCriteria criteria, Pageable pageable) {
        return visitRepository.search(
                criteria.customerEmail(),
                criteria.receptionistEmail(),
                criteria.active(),
                criteria.checkedInBefore(),
                criteria.checkedInAfter(),
                criteria.checkedOutBefore(),
                criteria.checkedOutAfter(),
                criteria.lockerNumber(),
                pageable
        );
    }

    @Override
    public int countUsedVisitsForMembership(Customer customer, Instant startsAt, Instant endsAt) {
        return (int)visitRepository.countByCustomerAndCheckedInAtBetween(customer, startsAt, endsAt);
    }
}
