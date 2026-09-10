package com.example.myauth.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.myauth.entities.User;
import com.example.myauth.exceptions.InvalidPasswordException;
import com.example.myauth.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please check your email.");
        }
        return user;
    }

    public User changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordLastChanged(Instant.now());
        return userRepository.save(user);
    }

}
