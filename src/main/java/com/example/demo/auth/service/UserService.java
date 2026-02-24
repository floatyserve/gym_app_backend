package com.example.demo.auth.service;

import com.example.demo.auth.domain.Role;
import com.example.demo.auth.domain.User;

import java.time.Instant;

public interface UserService {

    User findByEmail(String email);

    User findById(Long id);

    User create(String email, String rawPassword, Role role, Instant createdAt);

    void deactivate(Long currentUserId, Long targetUserId);

    void activate(Long currentUserId, Long targetUserId);

    void assertEmailAvailable(String email, Long currentUserId);
}
