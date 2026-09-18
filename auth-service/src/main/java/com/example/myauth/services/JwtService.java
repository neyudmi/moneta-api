package com.example.myauth.services;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.myauth.exceptions.RedisOperationException;

import com.example.myauth.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;
    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiration;
    // Check token is blacklisted, if not, blacklist it with expiration
    private static final DefaultRedisScript<Long> BLACKLIST_IF_NOT_PRESENT = new DefaultRedisScript<>(
            "if redis.call('EXISTS', KEYS[1]) == 1 then return 0; end; " +
                    "redis.call('SET', KEYS[1], '1', 'EX', ARGV[1]); return 1;",
            Long.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtService(RedisTemplate<String, String> redisTemplate, PrivateKey jwtPrivateKey, PublicKey jwtPublicKey) {
        this.redisTemplate = redisTemplate;
        this.privateKey = jwtPrivateKey;
        this.publicKey = jwtPublicKey;
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    // Nhóm build và generate

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .id(UUID.randomUUID().toString())
                .subject(userDetails.getUsername()) // lấy email làm subject
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey) // sign with private key
                .compact();
    }

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId().toString());
        extraClaims.put("tokenType", "access");
        return buildToken(extraClaims, user, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId().toString());
        extraClaims.put("tokenType", "refresh");
        return buildToken(extraClaims, user, refreshExpiration);
    }

    // Nhóm extract

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // extract email từ subject
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    // Nhóm validate

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = extractAllClaims(token);

            String extractedUsername = claims.getSubject();

            if (!extractedUsername.equals(userDetails.getUsername())
                    || !userDetails.isEnabled()
                    || claims.getExpiration().before(new Date())) {
                return false;
            }

            if (userDetails instanceof User user
                    && user.getPasswordLastChanged() != null
                    && !claims.getIssuedAt().toInstant()
                            .isAfter(user.getPasswordLastChanged())) {

                blacklistIfAbsent(
                        claims.getId(),
                        claims.getExpiration());

                return false;
            }

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Nhóm blacklist

    public String blacklistKey(String tokenId) {
        return "auth:blacklist:" + tokenId;
    }

    public boolean isBlacklisted(String tokenId) {
        if (tokenId == null || tokenId.isBlank()) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey(tokenId)));
        } catch (DataAccessException ex) {
            throw new RedisOperationException("Unable to check token blacklist.", ex);
        }
    }

    public boolean blacklistIfAbsent(String tokenId, Date expiration) {
        long ttlSeconds = Duration.ofMillis(expiration.getTime() -
                System.currentTimeMillis()).toSeconds();
        if (tokenId == null || tokenId.isBlank() || ttlSeconds <= 0) {
            return false;
        }
        try {
            Long result = redisTemplate.execute(BLACKLIST_IF_NOT_PRESENT,
                    List.of(blacklistKey(tokenId)),
                    String.valueOf(ttlSeconds));
            return Long.valueOf(1L).equals(result);
        } catch (DataAccessException ex) {
            throw new RedisOperationException("Unable to blacklist token.", ex);
        }
    }

    public void validateLogoutTokens(String accessToken, String refreshToken) {
        if (!"access".equals(extractTokenType(accessToken))
                || !"refresh".equals(extractTokenType(refreshToken))) {
            throw new BadCredentialsException("Invalid token type.");
        }
        if (isTokenExpired(accessToken) || isTokenExpired(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired token.");
        }
        if (extractTokenId(accessToken) == null
                || extractTokenId(refreshToken) == null) {
            throw new BadCredentialsException("Invalid token.");
        }
        if (!extractUsername(accessToken).equals(extractUsername(refreshToken))) {
            throw new BadCredentialsException("Access and refresh tokens do not belong together.");
        }
    }

}
