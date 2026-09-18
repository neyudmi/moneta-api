package com.example.expense_service.services;

import java.util.UUID;
import java.security.PublicKey;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    private final PublicKey publicKey;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtService(PublicKey jwtPublicKey, RedisTemplate<String, String> redisTemplate) {
        this.publicKey = jwtPublicKey;
        this.redisTemplate = redisTemplate;
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);

            if (!"access".equals(
                    claims.get("tokenType", String.class))) {
                return false;
            }

            String userId = claims.get("userId", String.class);

            if (userId == null || userId.isBlank()) {
                return false;
            }

            UUID.fromString(userId);

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String blacklistKey(String tokenId) {
        return "auth:blacklist:" + tokenId;
    }

    public boolean isBlacklisted(String tokenId) throws Exception {
        if (tokenId == null || tokenId.isBlank()) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey(tokenId)));
        } catch (DataAccessException ex) {
            throw new Exception("Unable to check token blacklist.", ex);
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(
                extractAllClaims(token)
                        .get("userId", String.class));
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractTokenId(String token) {
        return extractAllClaims(token).getId();
    }
}