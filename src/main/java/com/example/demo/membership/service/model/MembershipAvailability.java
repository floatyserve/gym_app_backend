package com.example.demo.membership.service.model;

import com.example.demo.membership.domain.Membership;

public record MembershipAvailability(
        Membership membership,
        Integer remainingVisits
) {}
