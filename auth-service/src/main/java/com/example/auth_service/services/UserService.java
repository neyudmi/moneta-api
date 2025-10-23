package com.example.auth_service.services;


import com.example.auth_service.models.User;

import java.util.List;

public interface UserService {
    List<User> allUsers();
    User findByUsername(String username);
}
