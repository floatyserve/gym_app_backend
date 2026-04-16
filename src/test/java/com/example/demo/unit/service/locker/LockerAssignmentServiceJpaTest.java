package com.example.demo.unit.service.locker;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.locker.domain.Locker;
import com.example.demo.locker.domain.LockerAssignment;
import com.example.demo.locker.repository.LockerAssignmentRepository;
import com.example.demo.locker.service.LockerService;
import com.example.demo.locker.service.impl.LockerAssignmentServiceJpa;
import com.example.demo.visit.domain.Visit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockerAssignmentServiceJpaTest {

    @Mock
    private LockerService lockerService;

    @Mock
    private LockerAssignmentRepository lockerAssignmentRepository;

    @InjectMocks
    private LockerAssignmentServiceJpa service;
    private final Instant NOW = Instant.parse("2025-01-01T10:00:00Z");

    @Test
    void assignLockerToVisitManually_createsAssignment() {
        Visit visit = mock(Visit.class);
        Locker locker = mock(Locker.class);

        when(visit.getId()).thenReturn(1L);
        when(lockerAssignmentRepository
                .existsByVisitIdAndReleasedAtIsNull(1L))
                .thenReturn(false);

        doNothing().when(lockerService).assertAvailable(locker);

        when(lockerAssignmentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LockerAssignment result =
                service.assignLockerToVisitManually(visit, locker, NOW);

        assertThat(result.getVisit()).isSameAs(visit);
        assertThat(result.getLocker()).isSameAs(locker);
    }


    @Test
    void reassignLocker_releasesOldAndAssignsNew() {
        Visit visit = mock(Visit.class);
        Locker newLocker = mock(Locker.class);
        LockerAssignment currentAssignment = mock(LockerAssignment.class);

        when(visit.getId()).thenReturn(1L);

        when(lockerAssignmentRepository
                .findByVisitIdAndReleasedAtIsNull(1L))
                .thenReturn(Optional.of(currentAssignment));

        doNothing().when(lockerService).assertAvailable(newLocker);

        when(lockerAssignmentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LockerAssignment result =
                service.reassignLocker(visit, newLocker, NOW);

        verify(currentAssignment).release(NOW);
        assertThat(result.getVisit()).isSameAs(visit);
        assertThat(result.getLocker()).isSameAs(newLocker);
    }

    @Test
    void assignLockerToVisitManually_throws_whenVisitAlreadyHasLocker() {
        Visit visit = mock(Visit.class);
        Locker locker = mock(Locker.class);

        when(visit.getId()).thenReturn(1L);
        when(lockerAssignmentRepository
                .existsByVisitIdAndReleasedAtIsNull(1L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.assignLockerToVisitManually(visit, locker, NOW))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already has a locker");
    }
}
