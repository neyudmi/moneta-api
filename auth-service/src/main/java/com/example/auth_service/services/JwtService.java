package com.example.auth_service.services;

import com.example.auth_service.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;
    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiration;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JacksonSerializer<Map<String, ?>> jwtJacksonSerializer;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(User userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userDetails.getId().toString());

        // Convert LocalDateTime to milliseconds
        extraClaims.put("passwordChangeAt",
                userDetails.getPasswordLastChanged()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli());

        return generateToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(User userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userDetails.getId().toString());

        // Convert LocalDateTime to milliseconds
        extraClaims.put("passwordChangeAt",
                userDetails.getPasswordLastChanged()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli());

        return generateToken(extraClaims, userDetails, refreshExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime) {
        return buildToken(extraClaims, userDetails, expirationTime);
    }

    public LocalDateTime getPasswordChangeAt(String token) {
        Long timestamp = extractClaim(token, claims -> claims.get("passwordChangeAt", Long.class));

        if (timestamp == null) {
            return null;
        }

        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }
    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .serializeToJsonWith(jwtJacksonSerializer)
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public void addToBlacklist(String token) {
        long expirationMillis = extractExpiration(token).getTime() - System.currentTimeMillis();
        if (expirationMillis > 0) {
            redisTemplate.opsForValue().set(token, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
