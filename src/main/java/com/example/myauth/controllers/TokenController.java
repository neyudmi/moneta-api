package com.example.myauth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.example.myauth.dtos.LogoutRequestDto;
import com.example.myauth.dtos.LoginResponseDto;
import com.example.myauth.entities.User;
import com.example.myauth.services.JwtService;

@RestController
@RequestMapping("/auth")
public class TokenController {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public TokenController(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshAccessToken(@RequestHeader("Authorization") String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing or invalid Authorization header.");
        }
        String refreshToken = authHeader.substring(7);
        String email = jwtService.extractUsername(refreshToken);
        User user = (User) userDetailsService.loadUserByUsername(email);

        // Check if the refresh token is valid and not expired
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Refresh token has expired.");
        }

        // Check type of token
        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BadCredentialsException("Invalid token type.");
        }

        // Return false if token already blacklisted
        boolean tokenBlacklisted = jwtService.blacklistIfAbsent(
                jwtService.extractTokenId(refreshToken),
                jwtService.extractExpiration(refreshToken));
        if (!tokenBlacklisted) {
            throw new BadCredentialsException("Refresh token has been revoked.");
        }
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        LoginResponseDto responseDto = new LoginResponseDto(
                user.getId(),
                newAccessToken,
                newRefreshToken,
                jwtService.getJwtExpiration());

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization") String authHeader,
            @Valid @RequestBody LogoutRequestDto request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing or invalid Authorization header.");
        }

        String accessToken = authHeader.substring(7).trim();
        String refreshToken = request.getRefreshToken().trim();
        if (accessToken.isEmpty() || refreshToken.isEmpty()) {
            throw new BadCredentialsException("Missing access or refresh token.");
        }

        jwtService.validateLogoutTokens(accessToken, refreshToken);

        jwtService.blacklistIfAbsent(
                jwtService.extractTokenId(accessToken),
                jwtService.extractExpiration(accessToken));
        jwtService.blacklistIfAbsent(
                jwtService.extractTokenId(refreshToken),
                jwtService.extractExpiration(refreshToken));
        return ResponseEntity.noContent().build();
    }

}
