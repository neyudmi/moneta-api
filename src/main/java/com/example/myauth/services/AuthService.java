package com.example.myauth.services;

import java.time.Instant;
import java.util.Locale;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauth.dtos.LoginRequestDto;
import com.example.myauth.dtos.RegisterRequestDto;
import com.example.myauth.entities.User;
import com.example.myauth.repositories.UserRepository;

import com.example.myauth.events.RegistracionEvent;
import com.example.myauth.exceptions.DuplicateEmailException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User register(RegisterRequestDto userDto) {
        String normalizedEmail = userDto.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new DuplicateEmailException();
        }
        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setBirthDay(userDto.getBirthDay());
        user.setGender(userDto.getGender());
        user.setEnabled(false);
        user.setPasswordLastChanged(Instant.now());
        try {
            User savedUser = userRepository.save(user);
            eventPublisher.publishEvent(new RegistracionEvent(savedUser));
            return savedUser;
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEmailException();
        }
    }

    public User login(LoginRequestDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail().trim().toLowerCase(Locale.ROOT), userDto.getPassword()));

        User user = (User) authentication.getPrincipal();

        return user;
    }

}
