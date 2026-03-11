package com.example.demo.customer.service.impl;

import com.example.demo.auth.domain.User;
import com.example.demo.common.ResourceType;
import com.example.demo.customer.api.dto.CustomerResponseDto;
import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.service.model.CustomerSearchCriteria;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer.service.CustomerService;
import com.example.demo.exceptions.AlreadyExistsException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceJpa implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer create(String fullName, String phoneNumber, String email, User createdBy) {
        if (customerRepository.existsByEmail(email)) {
            throw new AlreadyExistsException(ResourceType.CUSTOMER, "email");
        }

        if (customerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AlreadyExistsException(ResourceType.CUSTOMER, "phoneNumber");
        }

        return customerRepository.save(new Customer(fullName, phoneNumber, email, createdBy));
    }

    @Override
    public Page<CustomerResponseDto> search(
            CustomerSearchCriteria criteria,
            Pageable pageable
    ) {
        return customerRepository.search(
                criteria.fullName(),
                criteria.email(),
                criteria.phoneNumber(),
                criteria.cardCode(),
                pageable
        );
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ReferenceNotFoundException(ResourceType.CUSTOMER, "id"));
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ReferenceNotFoundException(ResourceType.CUSTOMER, "email"));
    }

    @Override
    public Customer update(Long id,
                       String fullName,
                       String phoneNumber,
                       String email,
                       User updatedBy
    ) {
        Customer customer = findById(id);

        customer.update(fullName, phoneNumber, email, updatedBy);

        return customer;
    }
}
