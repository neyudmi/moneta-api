package com.example.auth_service.controllers;

import com.example.auth_service.dtos.UserInfoResponseDto;
import com.example.auth_service.models.User;
import com.example.auth_service.repositories.UserRepository;
import com.example.auth_service.services.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}/password-last-changed")
    public ResponseEntity<LocalDateTime> getPasswordLastChangedTime(@PathVariable String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .map(user -> ResponseEntity.ok(user.getPasswordLastChanged()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserInfoResponseDto> authenticatedUser(@AuthenticationPrincipal User user){
        UserInfoResponseDto currentUser = getUserInfo(user);
        return ResponseEntity.ok(currentUser);
    }

     @Cacheable(value = "authenticatedUsers", key = "#user.id", unless = "#result == null")
    private UserInfoResponseDto getUserInfo(User user) {
        return new UserInfoResponseDto(user.getId(), user.getFullName(), user.getEmail(), user.getBirthDay(), user.getGender());
    }


}
