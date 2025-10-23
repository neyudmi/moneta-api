package com.example.auth_service.services;


import com.example.auth_service.models.User;
import com.example.auth_service.repositories.UserRepository;
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
    

    @Override
    public User findByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
    }
}
