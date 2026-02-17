package com.example.demo.card.service;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.card.domain.AccessCardStatus;
import com.example.demo.customer.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccessCardService {
    AccessCard findById(Long id);

    AccessCard findByCode(String number);

    AccessCard findActiveCard(Customer customer);

    AccessCard create(String number);

    Page<AccessCard> search(
            String code,
            AccessCardStatus status,
            Long customerId,
            Pageable pageable
    );

    Page<AccessCard> findByCustomer(Customer customer, Pageable pageable);
}
