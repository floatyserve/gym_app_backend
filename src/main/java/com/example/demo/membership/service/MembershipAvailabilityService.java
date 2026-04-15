package com.example.demo.membership.service;

import com.example.demo.common.ResourceType;
import com.example.demo.customer.domain.Customer;
import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.membership.domain.Membership;
import com.example.demo.membership.service.model.MembershipAvailability;
import com.example.demo.visit.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MembershipAvailabilityService {
    private final MembershipLifecycleService membershipService;
    private final VisitService visitService;
    private final Clock clock;

    public MembershipAvailability getAvailability(Customer customer) {
        Instant now = clock.instant();

        Membership membership = membershipService.findValidActiveMembership(customer, now)
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.MEMBERSHIP,
                        "active",
                        "No active membership found"
                ));

        Integer remainingVisits = null;

        if (membership.isLimited()) {
            int usedVisits = visitService.countUsedVisitsForMembership(
                    customer,
                    membership.getStartsAt(),
                    membership.getEndsAt()
            );

            remainingVisits = Math.max(0, membership.getVisitLimit() - usedVisits);
        }

        return new MembershipAvailability(membership, remainingVisits);
    }
}
