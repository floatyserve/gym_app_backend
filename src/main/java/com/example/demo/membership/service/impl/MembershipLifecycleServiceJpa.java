package com.example.demo.membership.service.impl;

import com.example.demo.common.ResourceType;
import com.example.demo.customer.domain.Customer;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.membership.domain.Membership;
import com.example.demo.membership.domain.MembershipDuration;
import com.example.demo.membership.domain.MembershipStatus;
import com.example.demo.membership.domain.MembershipType;
import com.example.demo.membership.repository.MembershipRepository;
import com.example.demo.membership.service.MembershipLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipLifecycleServiceJpa implements MembershipLifecycleService {

    private final MembershipRepository membershipRepository;

    @Override
    public Membership findById(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() ->
                        new ReferenceNotFoundException(ResourceType.MEMBERSHIP, "id")
                );
    }

    @Override
    public Optional<Membership> findValidActiveMembership(Customer customer, Instant now) {
        Optional<Membership> activeOptional = findActiveMembership(customer, now);

        if (activeOptional.isPresent()) {
            Membership membership = activeOptional.get();
            if (membership.getEndsAt().isBefore(now)) {
                membership.finishIfExpired(now);
                membershipRepository.save(membership);
                return Optional.empty();
            }
        }

        return activeOptional;
    }

    private Optional<Membership> findActiveMembership(Customer customer, Instant now) {
        return membershipRepository
                .findByCustomerAndStatusAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
                        customer,
                        MembershipStatus.ACTIVE,
                        now,
                        now
                );
    }

    @Override
    public Membership create(
            Customer customer,
            MembershipType type,
            MembershipDuration duration,
            Integer visitLimit
    ) {
        if (duration == null) {
            throw new BadRequestException(
                    ResourceType.MEMBERSHIP,
                    "duration",
                    "is required"
            );
        }

        validateVisitLimit(type, visitLimit);

        Membership membership = new Membership(
                customer,
                type,
                duration,
                visitLimit
        );

        return membershipRepository.save(membership);
    }

    @Override
    public Membership activateNextPendingMembership(Customer customer, Instant at) {
        if (membershipRepository.existsByCustomerAndStatus(customer, MembershipStatus.ACTIVE)) {
            throw new BadRequestException(
                    ResourceType.MEMBERSHIP,
                    "memberships",
                    "Customer already has an active membership"
            );
        }

        Membership pending = membershipRepository
                .findTopByCustomerAndStatusOrderByIdAsc(customer, MembershipStatus.PENDING)
                .orElseThrow(() ->
                        new BadRequestException(
                                ResourceType.MEMBERSHIP,
                                "memberships",
                                "Customer has no bought memberships"
                        )
                );

        pending.activate(at);

        return pending;
    }

    @Override
    public Page<Membership> findCustomerMemberships(Customer customer, Pageable pageable) {
        return membershipRepository.findByCustomer(customer, pageable);
    }

    @Override
    public void cancelMembership(Membership membership) {
        membership.cancel();
        membershipRepository.save(membership);
    }

    @Override
    public void finishMembership(Membership membership, Instant at) {
        membership.forceFinish(at);
        membershipRepository.save(membership);
    }

    private void validateVisitLimit(MembershipType type, Integer visitLimit) {
        if (type == MembershipType.LIMITED && visitLimit == null) {
            throw new BadRequestException(
                    ResourceType.MEMBERSHIP,
                    "visitLimit",
                    "is required for limited memberships"
            );
        }

        if (type != MembershipType.LIMITED && visitLimit != null) {
            throw new BadRequestException(
                    ResourceType.MEMBERSHIP,
                    "visitLimit",
                    "cannot be set for unlimited memberships"
            );
        }
    }
}

