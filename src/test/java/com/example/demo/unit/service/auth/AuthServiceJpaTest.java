package com.example.demo.unit.service.auth;

import com.example.demo.auth.domain.User;
import com.example.demo.auth.service.UserService;
import com.example.demo.auth.service.impl.AuthServiceJpa;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceJpaTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @Mock
    private User user;

    private AuthServiceJpa authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceJpa(
                authenticationManager,
                userService,
                passwordEncoder,
                jwtTokenProvider
        );
    }

    // ---------------- LOGIN ----------------

    @Test
    void login_returnsJwtToken_whenCredentialsAreValid() {
        String email = "test@test.com";
        String password = "password";
        String token = "jwt-token";

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        )).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtTokenProvider.generateToken(userPrincipal)).thenReturn(token);

        String result = authService.login(email, password);

        assertThat(result).isEqualTo(token);
    }

    @Test
    void login_throwsBadCredentialsException_whenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThatThrownBy(() ->
                authService.login("test@test.com", "wrong")
        ).isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtTokenProvider);
    }

    // ---------------- CHANGE PASSWORD ----------------

    @Test
    void changePassword_updatesPassword_andReturnsNewToken() {
        Long userId = 1L;
        String oldPassword = "OldPass123";
        String newPassword = "NewPass123";
        String encodedOld = "encoded-old";
        String encodedNew = "encoded-new";
        String token = "new-jwt";

        when(userService.findById(userId)).thenReturn(user);
        when(user.getPasswordHash()).thenReturn(encodedOld);

        when(passwordEncoder.matches(oldPassword, encodedOld)).thenReturn(true);
        when(passwordEncoder.matches(newPassword, encodedOld)).thenReturn(false);

        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNew);
        when(jwtTokenProvider.generateToken(any(UserPrincipal.class)))
                .thenReturn(token);

        String result = authService.changePassword(
                userId,
                oldPassword,
                newPassword
        );

        assertThat(result).isEqualTo(token);

        verify(user).setPasswordHash(encodedNew);
        verify(user).setPasswordChanged(true);
    }

    @Test
    void changePassword_throwsBadCredentialsException_whenOldPasswordIsWrong() {
        Long userId = 1L;
        String oldPassword = "wrong";
        String newPassword = "NewPass123";

        when(userService.findById(userId)).thenReturn(user);
        when(user.getPasswordHash()).thenReturn("encoded");

        when(passwordEncoder.matches(oldPassword, "encoded"))
                .thenReturn(false);

        assertThatThrownBy(() ->
                authService.changePassword(userId, oldPassword, newPassword)
        ).isInstanceOf(BadCredentialsException.class);

        verify(user, never()).setPasswordHash(any());
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void changePassword_throwsBadCredentialsException_whenNewPasswordIsSameAsOld() {
        Long userId = 1L;
        String password = "SamePass123";
        String encoded = "encoded";

        when(userService.findById(userId)).thenReturn(user);
        when(user.getPasswordHash()).thenReturn(encoded);

        when(passwordEncoder.matches(password, encoded)).thenReturn(true);

        assertThatThrownBy(() ->
                authService.changePassword(userId, password, password)
        ).isInstanceOf(BadRequestException.class)
                .hasMessageContaining("New password must be different from the old one");

        verify(user, never()).setPasswordHash(any());
        verify(jwtTokenProvider, never()).generateToken(any());
    }
}
