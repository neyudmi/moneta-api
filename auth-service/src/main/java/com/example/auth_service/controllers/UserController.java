package com.example.auth_service.controllers;
import com.example.auth_service.repositories.UserRepository;
import com.example.auth_service.services.UserService;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

}
