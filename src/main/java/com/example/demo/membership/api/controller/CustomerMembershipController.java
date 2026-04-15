package com.example.demo.membership.api.controller;

import com.example.demo.common.api.dto.PageResponseDto;
import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.service.CustomerService;
import com.example.demo.membership.api.dto.ActiveMembershipDto;
import com.example.demo.membership.api.dto.MembershipResponseDto;
import com.example.demo.membership.mapper.MembershipMapper;
import com.example.demo.membership.service.MembershipAvailabilityService;
import com.example.demo.membership.service.MembershipLifecycleService;
import com.example.demo.membership.service.model.MembershipAvailability;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/{customerId}/memberships")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
public class CustomerMembershipController {
    private final CustomerService customerService;
    private final MembershipLifecycleService membershipLifecycleService;
    private final MembershipMapper mapper;
    private final MembershipAvailabilityService membershipAvailabilityService;

    @GetMapping
    public PageResponseDto<MembershipResponseDto> getAllForCustomer(
            @PathVariable Long customerId,
            Pageable pageable
    ) {
        Customer customer = customerService.findById(customerId);

        Page<MembershipResponseDto> result =
                membershipLifecycleService.findCustomerMemberships(customer, pageable)
                        .map(mapper::toDto);

        return PageResponseDto.from(result);
    }

    @GetMapping("/active")
    public ActiveMembershipDto getActiveMembershipForCustomer(@PathVariable Long customerId) {
        Customer customer = customerService.findById(customerId);

        MembershipAvailability availability = membershipAvailabilityService.getAvailability(customer);

        return mapper.toActiveDto(availability.membership(), availability.remainingVisits());
    }
}
