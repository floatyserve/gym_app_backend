package com.example.demo.membership.service;

import com.example.demo.customer.domain.Customer;
import com.example.demo.membership.domain.Membership;
import com.example.demo.membership.domain.MembershipDuration;
import com.example.demo.membership.domain.MembershipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface MembershipLifecycleService {

    Membership findById(Long id);

    Optional<Membership> findValidActiveMembership(Customer customer, Instant at);

    Membership create(
            Customer customer,
            MembershipType type,
            MembershipDuration duration,
            Integer visitLimit
    );

    Membership activateNextPendingMembership(Customer customer, Instant at);

    Page<Membership> findCustomerMemberships(Customer customer, Pageable pageable);

    void cancelMembership(Membership membership);

    void finishMembership(Membership membership, Instant at);
}
