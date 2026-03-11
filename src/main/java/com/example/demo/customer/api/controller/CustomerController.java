package com.example.demo.customer.api.controller;

import com.example.demo.auth.domain.User;
import com.example.demo.auth.service.UserService;
import com.example.demo.common.api.dto.PageResponseDto;
import com.example.demo.customer.api.dto.CreateCustomerRequest;
import com.example.demo.customer.api.dto.CustomerResponseDto;
import com.example.demo.customer.api.dto.CustomerSearchRequest;
import com.example.demo.customer.api.dto.UpdateCustomerRequest;
import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.service.model.CustomerSearchCriteria;
import com.example.demo.customer.mapper.CustomerMapper;
import com.example.demo.customer.service.CustomerService;
import com.example.demo.customer.service.RegistrationService;
import com.example.demo.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
public class CustomerController {
    private final CustomerService customerService;
    private final UserService userService;
    private final CustomerMapper customerMapper;
    private final RegistrationService registrationService;

    @GetMapping
    public PageResponseDto<CustomerResponseDto> search(
            @ModelAttribute CustomerSearchRequest request,
            Pageable pageable
    ) {
        CustomerSearchCriteria criteria = new CustomerSearchCriteria(
                request.fullName(),
                request.email(),
                request.phoneNumber(),
                request.cardCode()
        );

        return PageResponseDto.from(
                customerService.search(criteria, pageable)
        );
    }

    @GetMapping("/{id}")
    public CustomerResponseDto getById(@PathVariable Long id) {
        return customerMapper.toDto(customerService.findById(id));
    }

    @GetMapping("/by-email")
    public CustomerResponseDto getByEmail(@RequestParam String email) {
        return customerMapper.toDto(customerService.findByEmail(email));
    }

    @PostMapping("/register")
    public CustomerResponseDto registerNewCustomer(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid CreateCustomerRequest request
    ) {
        User createdBy = userService.findById(userPrincipal.getId());

        Customer customer = registrationService.registerCustomerWithCard(
                request.fullName(),
                request.phoneNumber(),
                request.email(),
                createdBy,
                request.cardCode()
        );

        return customerMapper.toDto(customer);
    }

    @PatchMapping("/{id}")
    public CustomerResponseDto updateCustomer(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request
    ) {
        User updatedBy = userService.findById(userPrincipal.getId());

        Customer updatedCustomer = customerService.update(
                id,
                request.fullName(),
                request.phoneNumber(),
                request.email(),
                updatedBy
        );

        return customerMapper.toDto(updatedCustomer);
    }
}
