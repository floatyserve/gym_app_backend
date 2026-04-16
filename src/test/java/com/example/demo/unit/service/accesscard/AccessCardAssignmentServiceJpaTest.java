package com.example.demo.unit.service.accesscard;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.card.domain.AccessCardStatus;
import com.example.demo.card.repository.AccessCardRepository;
import com.example.demo.card.service.impl.AccessCardAssignmentServiceJpa;
import com.example.demo.customer.domain.Customer;
import com.example.demo.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessCardAssignmentServiceJpaTest {

    @Mock
    private AccessCardRepository accessCardRepository;

    @InjectMocks
    private AccessCardAssignmentServiceJpa service;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("John Doe", "1234567890", "john@example.com", null);
    }

    @Test
    void assignCard_success() {
        AccessCard card = new AccessCard("CARD-1");

        when(accessCardRepository.existsByCustomerAndStatus(customer, AccessCardStatus.ACTIVE))
                .thenReturn(false);
        when(accessCardRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccessCard assigned = service.assignCard(card, customer);

        assertEquals(customer, assigned.getCustomer());
        assertEquals(AccessCardStatus.ACTIVE, assigned.getStatus());
        verify(accessCardRepository).save(card);
    }

    @Test
    void assignCard_customerAlreadyHasActiveCard() {
        AccessCard card = new AccessCard("CARD-1");

        when(accessCardRepository.existsByCustomerAndStatus(customer, AccessCardStatus.ACTIVE))
                .thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.assignCard(card, customer));
    }

    @Test
    void detachFromCustomer_success() {
        AccessCard card = new AccessCard("CARD-1");
        card.assign(customer);
        card.activate();

        when(accessCardRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccessCard detached = service.detachFromCustomer(card);

        assertNull(detached.getCustomer());
        assertEquals(AccessCardStatus.INACTIVE, detached.getStatus());
        verify(accessCardRepository).save(card);
    }
}