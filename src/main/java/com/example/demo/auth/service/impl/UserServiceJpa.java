package com.example.demo.auth.service.impl;

import com.example.demo.auth.domain.Role;
import com.example.demo.auth.domain.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.service.UserService;
import com.example.demo.common.ResourceType;
import com.example.demo.exceptions.AlreadyExistsException;
import com.example.demo.exceptions.ReferenceNotFoundException;
import com.example.demo.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceJpa implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.USER,
                        "email"
                ));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ReferenceNotFoundException(
                        ResourceType.USER,
                        "id"
                ));
    }

    @Override
    public User create(String email, String rawPassword, Role role, Instant at) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException(ResourceType.USER, "email");
        }

        User user = new User(
                email,
                passwordEncoder.encode(rawPassword),
                role,
                at
        );

        return userRepository.save(user);
    }

    @Override
    public void deactivate(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException(
                    ResourceType.USER,
                    "active",
                    "You cannot deactivate yourself"
            );
        }

        User user = findById(targetUserId);
        user.deactivate();
    }

    @Override
    public void activate(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException(
                    ResourceType.USER,
                    "active",
                    "You cannot activate yourself"
            );
        }

        User user = findById(targetUserId);
        user.activate();
    }

    @Override
    public void assertEmailAvailable(String email, Long currentUserId) {
        userRepository.findByEmail(email)
                .filter(u -> !u.getId().equals(currentUserId))
                .ifPresent(_ -> {
                    throw new AlreadyExistsException(ResourceType.USER, "email");
                });
    }
}
