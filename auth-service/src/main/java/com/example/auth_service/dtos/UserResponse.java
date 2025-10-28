package com.example.auth_service.dtos;

import java.time.LocalDateTime;

public class UserResponse {
    private String username;
    private LocalDateTime passwordLastChanged;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getPasswordLastChanged() {
        return passwordLastChanged;
    }

    public void setPasswordLastChanged(LocalDateTime passwordLastChanged) {
        this.passwordLastChanged = passwordLastChanged;
    }
}
