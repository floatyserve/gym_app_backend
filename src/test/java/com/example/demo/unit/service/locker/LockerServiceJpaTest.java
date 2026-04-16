package com.example.demo.unit.service.locker;

import com.example.demo.exceptions.AlreadyExistsException;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.locker.domain.Locker;
import com.example.demo.locker.domain.LockerStatus;
import com.example.demo.locker.repository.LockerAssignmentRepository;
import com.example.demo.locker.repository.LockerRepository;
import com.example.demo.locker.service.impl.LockerServiceJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockerServiceJpaTest {

    @Mock
    private LockerRepository lockerRepository;

    @Mock
    private LockerAssignmentRepository lockerAssignmentRepository;

    @InjectMocks
    private LockerServiceJpa lockerService;

    @Test
    void findById_returnsLocker_whenExists() {
        Locker locker = mock(Locker.class);
        when(lockerRepository.findById(1L)).thenReturn(Optional.of(locker));

        Locker result = lockerService.findById(1L);

        assertThat(result).isSameAs(locker);
    }

    @Test
    void findById_throwsException_whenNotFound() {
        when(lockerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lockerService.findById(1L))
                .isInstanceOf(ReferenceNotFoundException.class)
                .hasMessageContaining("Locker not found for field id");
    }

    @Test
    void create_createsLocker_whenNumberIsUnique() {
        when(lockerRepository.existsByNumber(10)).thenReturn(false);
        when(lockerRepository.save(any(Locker.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Locker locker = lockerService.create(10);

        assertThat(locker.getNumber()).isEqualTo(10);
        assertThat(locker.getStatus()).isEqualTo(LockerStatus.AVAILABLE);
    }

    @Test
    void create_throwsException_whenNumberExists() {
        when(lockerRepository.existsByNumber(10)).thenReturn(true);

        assertThatThrownBy(() -> lockerService.create(10))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Locker already exists for field number");
    }

    @Test
    void assertAvailable_passes_whenLockerIsAvailableAndFree() {
        Locker locker = mock(Locker.class);
        when(locker.getStatus()).thenReturn(LockerStatus.AVAILABLE);
        when(locker.getId()).thenReturn(1L);
        when(lockerAssignmentRepository.existsByLockerIdAndReleasedAtIsNull(1L))
                .thenReturn(false);

        lockerService.assertAvailable(locker);
    }

    @Test
    void assertAvailable_throwsException_whenOutOfOrder() {
        Locker locker = mock(Locker.class);
        when(locker.getStatus()).thenReturn(LockerStatus.OUT_OF_ORDER);

        assertThatThrownBy(() -> lockerService.assertAvailable(locker))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Locker is not available");
    }

    @Test
    void assertAvailable_throwsException_whenOccupied() {
        Locker locker = mock(Locker.class);
        when(locker.getStatus()).thenReturn(LockerStatus.AVAILABLE);
        when(locker.getId()).thenReturn(1L);
        when(lockerAssignmentRepository.existsByLockerIdAndReleasedAtIsNull(1L))
                .thenReturn(true);

        assertThatThrownBy(() -> lockerService.assertAvailable(locker))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Locker is currently occupied");
    }

    @Test
    void outOfOrder_marksLockerOutOfOrder() {
        Locker locker = new Locker(1, LockerStatus.AVAILABLE);

        ReflectionTestUtils.setField(locker, "id", 1L);

        when(lockerRepository.findById(1L))
                .thenReturn(Optional.of(locker));

        when(lockerAssignmentRepository.existsByLockerIdAndReleasedAtIsNull(1L))
                .thenReturn(false);

        Locker result = lockerService.outOfOrder(locker);

        assertThat(result.getStatus()).isEqualTo(LockerStatus.OUT_OF_ORDER);
        assertThat(result).isSameAs(locker);
    }

}
