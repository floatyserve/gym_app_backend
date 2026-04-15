package com.example.demo.customer.api.dto;

import com.example.demo.membership.domain.MembershipDuration;
import com.example.demo.membership.domain.MembershipStatus;
import com.example.demo.membership.domain.MembershipType;

import java.time.Instant;

public record FrontDeskCheckInDto(
        Long customerId,
        String fullName,
        String email,
        String activeCardCode,

        MembershipType membershipType,
        MembershipStatus membershipStatus,
        MembershipDuration membershipDuration,
        Integer visitLimit,
        Integer remainingVisits,
        Instant startsAt,
        Instant endsAt
) { }
