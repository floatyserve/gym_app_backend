package com.example.demo.unit.service.accesscard;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.card.domain.AccessCardStatus;
import com.example.demo.card.repository.AccessCardRepository;
import com.example.demo.card.service.impl.AccessCardServiceJpa;
import com.example.demo.customer.domain.Customer;
import com.example.demo.exceptions.AlreadyExistsException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessCardServiceJpaTest {

    @Mock
    private AccessCardRepository accessCardRepository;

    @InjectMocks
    private AccessCardServiceJpa service;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("John Doe", "1234567890", "john@example.com", null);
    }

    @Test
    void findById_found() {
        AccessCard card = new AccessCard("CARD-1");
        when(accessCardRepository.findById(1L)).thenReturn(Optional.of(card));

        AccessCard result = service.findById(1L);

        assertEquals(card, result);
    }

    @Test
    void findById_notFound() {
        when(accessCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReferenceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void findByCode_found() {
        AccessCard card = new AccessCard("CARD-1");
        when(accessCardRepository.findByCode("CARD-1")).thenReturn(Optional.of(card));

        AccessCard result = service.findByCode("CARD-1");

        assertEquals(card, result);
    }

    @Test
    void findByCode_notFound() {
        when(accessCardRepository.findByCode("CARD-1")).thenReturn(Optional.empty());

        assertThrows(ReferenceNotFoundException.class, () -> service.findByCode("CARD-1"));
    }

    @Test
    void findActiveCard_returnsCard() {
        AccessCard card = new AccessCard("CARD-1");
        card.assign(customer);
        card.activate();

        when(accessCardRepository.findByCustomerAndStatus(customer, AccessCardStatus.ACTIVE))
                .thenReturn(card);

        AccessCard result = service.findActiveCard(customer);

        assertEquals(card, result);
    }

    @Test
    void create_success() {
        when(accessCardRepository.existsByCode("CARD-1")).thenReturn(false);
        when(accessCardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccessCard card = service.create("CARD-1");

        assertEquals("CARD-1", card.getCode());
        assertEquals(AccessCardStatus.INACTIVE, card.getStatus());
        verify(accessCardRepository).save(card);
    }

    @Test
    void create_duplicateCode() {
        when(accessCardRepository.existsByCode("CARD-1")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> service.create("CARD-1"));
    }

    @Test
    void findByCustomer_returnsPage() {
        Page<AccessCard> page = new PageImpl<>(List.of(new AccessCard("C1")));

        when(accessCardRepository.findByCustomer(customer, Pageable.unpaged())).thenReturn(page);

        Page<AccessCard> result = service.findByCustomer(customer, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }
}