package com.example.myauth.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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
import com.example.myauth.utils.RandomVerificationCode;
import com.example.myauth.events.RegistracionEvent;
import com.example.myauth.exceptions.DuplicateEmailException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthenticationManager authenticationManager;
    private static final Duration CODE_TTL = Duration.ofMinutes(10);
    private static final Duration REQUEST_COOLDOWN = Duration.ofMinutes(1);
    private static final DefaultRedisScript<Long> CONSUME_CODE = new DefaultRedisScript<>(
            "if redis.call('GET', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('DEL', KEYS[1]); end; return 0;",
            Long.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher, AuthenticationManager authenticationManager,
            RedisTemplate<String, String> redisTemplate, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
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

    public void sendPasswordResetEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is not enabled. Please verify your email first.");
        }

        try {
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
                    "password-reset-cooldown:" + normalizedEmail, "1", REQUEST_COOLDOWN);
            if (Boolean.FALSE.equals(acquired)) {
                throw new IllegalArgumentException(
                        "Password reset request is on cooldown. Please wait before trying again.");
            }

            String code = RandomVerificationCode.generateCode();
            redisTemplate.opsForValue().set("password-reset-code:" + normalizedEmail, code, CODE_TTL);
            emailService.sendPasswordResetEmail(normalizedEmail, user.getFullName(), code);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Unable to create password reset request.", ex);
        }
    }

    public void resetPassword(String email, String code, String newPassword) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset code."));
        try {
            Long consumed = redisTemplate.execute(
                    CONSUME_CODE,
                    List.of("password-reset-code:" + normalizedEmail),
                    code);
            if (consumed == null || consumed == 0) {
                throw new IllegalArgumentException("Invalid or expired password reset code.");
            }
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Unable to validate password reset code.", ex);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordLastChanged(Instant.now());
        userRepository.save(user);
    }

}
