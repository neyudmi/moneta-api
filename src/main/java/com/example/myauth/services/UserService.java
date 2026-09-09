package com.example.myauth.services;

import org.springframework.stereotype.Service;

import com.example.myauth.entities.User;
import com.example.myauth.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please check your email.");
        }
        return user;
    }

}
