package com.example.demo.membership.api.dto;

import com.example.demo.membership.domain.MembershipDuration;
import com.example.demo.membership.domain.MembershipStatus;
import com.example.demo.membership.domain.MembershipType;

import java.time.Instant;

public record ActiveMembershipDto(
    Long id,
    String customerFullName,
    String customerEmail,
    MembershipType type,
    MembershipDuration duration,
    Integer visitLimit,
    Integer remainingVisits,
    MembershipStatus status,
    Instant startsAt,
    Instant endsAt
)
{}
