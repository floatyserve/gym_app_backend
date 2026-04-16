package com.example.demo.unit.service.visit;

import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.visit.domain.Visit;
import com.example.demo.visit.repository.VisitRepository;
import com.example.demo.visit.service.impl.VisitServiceJpa;
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
class VisitServiceJpaTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitServiceJpa visitService;

    @Test
    void findById_returnsVisit_whenExists() {
        Visit visit = mock(Visit.class);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        Visit result = visitService.findById(1L);

        assertThat(result).isSameAs(visit);
    }

    @Test
    void findById_throwsException_whenNotFound() {
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.findById(1L))
                .isInstanceOf(ReferenceNotFoundException.class)
                .hasMessageContaining("Visit not found for field id");
    }

    @Test
    void findActiveVisit_returnsVisit_whenActive() {
        Visit visit = mock(Visit.class);
        when(visit.getCheckedOutAt()).thenReturn(null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        Visit result = visitService.findActiveVisit(1L);

        assertThat(result).isSameAs(visit);
    }

    @Test
    void findActiveVisit_throwsException_whenCheckedOut() {
        Visit visit = mock(Visit.class);
        when(visit.getCheckedOutAt()).thenReturn(Instant.now());
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> visitService.findActiveVisit(1L))
                .isInstanceOf(ReferenceNotFoundException.class)
                .hasMessageContaining("Requested visit is already checked out");
    }
}
