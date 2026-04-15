package com.example.demo.customer.api.controller;

import com.example.demo.customer.api.dto.FrontDeskCheckInDto;
import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.mapper.FrontDeskMapper;
import com.example.demo.customer.service.CustomerService;
import com.example.demo.membership.service.MembershipAvailabilityService;
import com.example.demo.membership.service.model.MembershipAvailability;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check-in")
@RequiredArgsConstructor
public class FrontDeskCheckInController {

    private final CustomerService customerService;
    private final MembershipAvailabilityService availabilityService;
    private final FrontDeskMapper mapper;

    @GetMapping("/scan/{cardCode}")
    public FrontDeskCheckInDto scanCard(@PathVariable String cardCode) {
        Customer customer = customerService.findByAccessCardCode(cardCode);

        MembershipAvailability availability = availabilityService.getAvailability(customer);

        return mapper.toCheckInDto(customer, cardCode, availability);
    }

    @GetMapping("/email/{email}")
    public FrontDeskCheckInDto scanEmail(@PathVariable String email) {
        Customer customer = customerService.findByEmail(email);

        MembershipAvailability availability = availabilityService.getAvailability(customer);

        return mapper.toCheckInDto(customer, null, availability);
    }
}
