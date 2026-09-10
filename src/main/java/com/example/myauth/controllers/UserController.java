package com.example.myauth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.myauth.services.UserService;
import com.example.myauth.exceptions.InvalidPasswordException;

import jakarta.validation.Valid;

import com.example.myauth.dtos.ChangePasswordRequestDto;
import com.example.myauth.dtos.InforUserDto;
import com.example.myauth.entities.User;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<InforUserDto> getUserInfo(@AuthenticationPrincipal User user) {
        InforUserDto userInfo = new InforUserDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getBirthDay(),
                user.getGender());
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequestDto requestDto) {
        try {
            userService.changePassword(user.getId(), requestDto.getCurrentPassword(), requestDto.getNewPassword());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Password changed successfully.");
    }

}
