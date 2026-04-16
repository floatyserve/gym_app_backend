package com.example.demo.unit.service.membership;

import com.example.demo.customer.domain.Customer;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.membership.domain.Membership;
import com.example.demo.membership.domain.MembershipStatus;
import com.example.demo.membership.domain.MembershipType;
import com.example.demo.membership.domain.MembershipDuration;
import com.example.demo.membership.repository.MembershipRepository;
import com.example.demo.membership.service.impl.MembershipLifecycleServiceJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipLifecycleServiceJpaTest {

    @Mock
    private MembershipRepository membershipRepository;

    @InjectMocks
    private MembershipLifecycleServiceJpa service;

    private Customer customer;
    private Instant NOW;

    @BeforeEach
    void setUp() {
        customer = new Customer("John Doe", "1234567890", "john@example.com", null);
        NOW = Instant.parse("2026-01-01T10:00:00Z");
    }

    @Test
    void findById_found() {
        Membership membership = new Membership(customer, MembershipType.UNLIMITED, MembershipDuration.MONTH, null);
        membership.activate(NOW);
        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));

        Membership result = service.findById(1L);

        assertEquals(membership, result);
    }

    @Test
    void findById_notFound() {
        when(membershipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReferenceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void findActiveMembership_present() {
        Membership active = new Membership(customer, MembershipType.UNLIMITED, MembershipDuration.MONTH, null);
        active.activate(NOW);
        when(membershipRepository.findByCustomerAndStatusAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
                eq(customer), eq(MembershipStatus.ACTIVE), eq(NOW), eq(NOW)
        )).thenReturn(Optional.of(active));


        Optional<Membership> result = service.findValidActiveMembership(customer, NOW);

        assertTrue(result.isPresent());
        assertEquals(active, result.get());
    }

    @Test
    void findActiveMembership_absent() {
        when(membershipRepository.findByCustomerAndStatusAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
                eq(customer), eq(MembershipStatus.ACTIVE), eq(NOW), eq(NOW)
        )).thenReturn(Optional.empty());

        Optional<Membership> result = service.findValidActiveMembership(customer, NOW);

        assertFalse(result.isPresent());
    }

    @Test
    void create_validLimitedMembership() {
        when(membershipRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Membership membership = service.create(customer, MembershipType.LIMITED, MembershipDuration.MONTH, 10);

        assertEquals(MembershipStatus.PENDING, membership.getStatus());
        assertEquals(10, membership.getVisitLimit());
        verify(membershipRepository).save(membership);
    }

    @Test
    void create_validUnlimitedMembership() {
        when(membershipRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Membership membership = service.create(customer, MembershipType.UNLIMITED, MembershipDuration.MONTH, null);

        assertEquals(MembershipStatus.PENDING, membership.getStatus());
        assertNull(membership.getVisitLimit());
        verify(membershipRepository).save(membership);
    }

    @Test
    void create_invalidDuration() {
        assertThrows(BadRequestException.class, () -> service.create(customer, MembershipType.UNLIMITED, null, null));
    }

    @Test
    void create_invalidVisitLimitForLimited() {
        assertThrows(BadRequestException.class,
                () -> service.create(customer, MembershipType.LIMITED, MembershipDuration.MONTH, null));
    }

    @Test
    void create_invalidVisitLimitForUnlimited() {
        assertThrows(BadRequestException.class,
                () -> service.create(customer, MembershipType.UNLIMITED, MembershipDuration.MONTH, 5));
    }

    @Test
    void activateNextPendingMembership_success() {
        Membership pending = new Membership(customer, MembershipType.UNLIMITED, MembershipDuration.MONTH, null);

        when(membershipRepository.existsByCustomerAndStatus(customer, MembershipStatus.ACTIVE)).thenReturn(false);
        when(membershipRepository.findTopByCustomerAndStatusOrderByIdAsc(customer, MembershipStatus.PENDING))
                .thenReturn(Optional.of(pending));

        Membership activated = service.activateNextPendingMembership(customer, NOW);

        assertEquals(MembershipStatus.ACTIVE, activated.getStatus());
        assertEquals(NOW, activated.getStartsAt());
        assertEquals(activated.getEndsAt(), MembershipDuration.MONTH.addTo(NOW));
    }

    @Test
    void activateNextPendingMembership_alreadyActive() {
        when(membershipRepository.existsByCustomerAndStatus(customer, MembershipStatus.ACTIVE)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.activateNextPendingMembership(customer, NOW));
    }

    @Test
    void activateNextPendingMembership_noPending() {
        when(membershipRepository.existsByCustomerAndStatus(customer, MembershipStatus.ACTIVE)).thenReturn(false);
        when(membershipRepository.findTopByCustomerAndStatusOrderByIdAsc(customer, MembershipStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> service.activateNextPendingMembership(customer, NOW));
    }

    @Test
    void findCustomerMemberships_returnsPage() {
        Membership m1 = new Membership(customer, MembershipType.UNLIMITED, MembershipDuration.MONTH, null);
        Membership m2 = new Membership(customer, MembershipType.LIMITED, MembershipDuration.YEAR, 5);

        Page<Membership> page = new PageImpl<>(List.of(m1, m2));
        when(membershipRepository.findByCustomer(customer, Pageable.unpaged())).thenReturn(page);

        Page<Membership> result = service.findCustomerMemberships(customer, Pageable.unpaged());

        assertEquals(2, result.getTotalElements());
        verify(membershipRepository).findByCustomer(customer, Pageable.unpaged());
    }
}