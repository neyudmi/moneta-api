package com.example.myauth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.myauth.services.UserService;

import jakarta.validation.Valid;

import com.example.myauth.dtos.ChangePasswordRequestDto;
import com.example.myauth.dtos.InforUserDto;
import com.example.myauth.dtos.UpdateUserRequestDto;
import com.example.myauth.entities.User;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<InforUserDto> getUserInfo(@AuthenticationPrincipal User user) {
        InforUserDto userInfo = new InforUserDto(
                user.getFullName(),
                user.getEmail(),
                user.getBirthDay(),
                user.getGender());
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequestDto requestDto) {
        userService.changePassword(user.getId(), requestDto.getCurrentPassword(), requestDto.getNewPassword());
        return ResponseEntity.ok("Password changed successfully.");
    }

    @PutMapping("/me")
    public ResponseEntity<UpdateUserRequestDto> updateUserInfo(@AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequestDto requestDto) {
        User updatedUser = userService.updateUser(user.getId(), requestDto.getFullName(), requestDto.getBirthDay(),
                requestDto.getGender());

        UpdateUserRequestDto response = new UpdateUserRequestDto(
                updatedUser.getFullName(),
                updatedUser.getBirthDay(),
                updatedUser.getGender());

        return ResponseEntity.ok(response);
    }

}
