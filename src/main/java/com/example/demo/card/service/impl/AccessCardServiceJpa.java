package com.example.demo.card.service.impl;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.card.domain.AccessCardStatus;
import com.example.demo.card.repository.AccessCardRepository;
import com.example.demo.card.repository.specification.AccessCardSpecification;
import com.example.demo.card.service.AccessCardService;
import com.example.demo.card.service.model.AccessCardSearchCriteria;
import com.example.demo.common.ResourceType;
import com.example.demo.customer.domain.Customer;
import com.example.demo.exceptions.AlreadyExistsException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessCardServiceJpa implements AccessCardService {
    private final AccessCardRepository accessCardRepository;

    @Override
    public AccessCard findById(Long id) {
        return accessCardRepository.findById(id)
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.ACCESS_CARD,
                        "id"
                ));
    }

    @Override
    public AccessCard findByCode(String code) {
        return accessCardRepository.findByCode(code)
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.ACCESS_CARD,
                        "code"
                ));
    }

    @Override
    public AccessCard findActiveCard(Customer customer) {
        return accessCardRepository.findByCustomerAndStatus(customer, AccessCardStatus.ACTIVE);
    }

    @Override
    public AccessCard create(String code) {
        if(accessCardRepository.existsByCode(code)) {
            throw new AlreadyExistsException(ResourceType.ACCESS_CARD, "code");
        }

        return accessCardRepository.save(new AccessCard(code));
    }

    @Override
    public Page<AccessCard> search(AccessCardSearchCriteria criteria, Pageable pageable) {
        Specification<AccessCard> specification = AccessCardSpecification.build(criteria);

        return accessCardRepository.findAll(specification, pageable);
    }

    @Override
    public Page<AccessCard> findByCustomer(Customer customer, Pageable pageable) {
        return accessCardRepository.findByCustomer(customer, pageable);
    }
}
