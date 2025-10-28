package com.example.auth_service.services;

import com.example.auth_service.dtos.LoginUserDto;
import com.example.auth_service.dtos.RegisterUserDto;
import com.example.auth_service.models.User;
import com.example.auth_service.events.UserCreatedEvent;
import com.example.auth_service.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {  
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final EventPublisherService eventPublisherService;

    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            EventPublisherService eventPublisherService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.eventPublisherService = eventPublisherService;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setGender(input.getGender());
        user.setBirthDay(input.getBirthDay());
        user.setPassword(input.getPassword());
        user.setPasswordLastChanged(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        
        // Publish user created event
        UserCreatedEvent event = new UserCreatedEvent(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail()
        );
        eventPublisherService.publishUserCreatedEvent(event);
        
        return savedUser;
    }

    public User authenticate (LoginUserDto input){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                input.getEmail(), 
                input.getPassword()
            )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }


    public User loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
