package com.example.auth_service.dtos;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long accessExpiresIn;
    private UUID userId;

    public LoginResponse(String accessToken, String refreshToken, long accessExpiresIn, UUID userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessExpiresIn = accessExpiresIn;
        this.userId = userId;
    }

    public LoginResponse() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getAccessExpiresIn() {
        return accessExpiresIn;
    }

    public void setAccessExpiresIn(long accessExpiresIn) {
        this.accessExpiresIn = accessExpiresIn;
    }

}
