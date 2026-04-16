package com.example.demo.unit.service.auth;

import com.example.demo.auth.domain.Role;
import com.example.demo.auth.domain.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.service.impl.UserServiceJpa;
import com.example.demo.exceptions.AlreadyExistsException;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceJpa userService;

    private final Instant NOW = Instant.parse("2025-01-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        userService = new UserServiceJpa(userRepository, passwordEncoder);
    }

    // ---------- create ----------

    @Test
    void create_createsUser_whenEmailIsNotUsed() {
        String email = "test@test.com";
        String rawPassword = "password";
        String encodedPassword = "encoded";
        Role role = Role.RECEPTIONIST;

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.create(email, rawPassword, role, NOW);

        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPasswordHash()).isEqualTo(encodedPassword);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void create_throwsException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() ->
                userService.create("test@test.com", "password", Role.RECEPTIONIST, NOW)
        ).isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("User already exists for field email");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    // ---------- findById ----------

    @Test
    void findById_returnsUser_whenUserExists() {
        User user = new User("a@test.com", "pw", Role.RECEPTIONIST, NOW);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result).isSameAs(user);
    }

    @Test
    void findById_throwsException_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(ReferenceNotFoundException.class)
                .hasMessageContaining("User not found for field id");
    }

    // ---------- findByEmail ----------

    @Test
    void findByEmail_returnsUser_whenUserExists() {
        User user = new User("a@test.com", "pw", Role.RECEPTIONIST, NOW);
        when(userRepository.findByEmail("a@test.com"))
                .thenReturn(Optional.of(user));

        User result = userService.findByEmail("a@test.com");

        assertThat(result).isSameAs(user);
    }

    @Test
    void findByEmail_throwsException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("a@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("a@test.com"))
                .isInstanceOf(ReferenceNotFoundException.class)
                .hasMessageContaining("email");
    }

    // ---------- activate / deactivate ----------

    @Test
    void deactivate_marksTargetUserAsInactive_whenDifferentUser() {
        Long adminId = 1L;
        Long targetUserId = 2L;

        User user = new User("a@test.com", "pw", Role.RECEPTIONIST, NOW);
        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(user));

        userService.deactivate(adminId, targetUserId);

        assertThat(user.isActive()).isFalse();
    }

    @Test
    void deactivate_throwsBadRequestException_whenUserTriesToDeactivateSelf() {
        Long userId = 1L;

        assertThatThrownBy(() ->
                userService.deactivate(userId, userId)
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("You cannot deactivate yourself");

        verify(userRepository, never()).findById(any());
    }

    @Test
    void activate_marksTargetUserAsActive_whenDifferentUser() {
        Long adminId = 1L;
        Long targetUserId = 2L;

        User user = new User("a@test.com", "pw", Role.RECEPTIONIST, NOW);
        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(user));

        userService.activate(adminId, targetUserId);

        assertThat(user.isActive());
    }

    @Test
    void activate_throwsBadRequestException_whenUserTriesToActivateSelf() {
        Long userId = 1L;

        assertThatThrownBy(() ->
                userService.activate(userId, userId)
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("You cannot activate yourself");

        verify(userRepository, never()).findById(any());
    }
}
