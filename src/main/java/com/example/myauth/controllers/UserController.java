package com.example.myauth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myauth.dtos.InforUserDto;
import com.example.myauth.entities.User;

@RestController
@RequestMapping("/users")
public class UserController {

    public UserController() {
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

}
