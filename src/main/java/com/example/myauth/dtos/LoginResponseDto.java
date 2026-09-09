package com.example.myauth.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private UUID userId;
    private String accessToken;
    private String refreshToken;
    private long accessExpiresIn;
}
