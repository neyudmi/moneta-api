package com.example.auth_service.services;
import com.example.auth_service.models.User;
import com.example.auth_service.repositories.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;

    public UserServiceImplement(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @CacheEvict(value = "authenticatedUsers", key = "#id")
    
   @Override
    public User findByUsername(String username) {
        User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        if (user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please check your email.");
        }
        return user;
    }

}
