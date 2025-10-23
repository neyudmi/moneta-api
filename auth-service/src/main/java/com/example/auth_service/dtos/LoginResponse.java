package com.example.auth_service.dtos;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long accessExpiresIn;

    
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
