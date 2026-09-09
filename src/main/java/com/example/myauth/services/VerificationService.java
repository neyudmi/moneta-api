package com.example.myauth.services;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.myauth.entities.User;
import com.example.myauth.exceptions.RedisOperationException;
import com.example.myauth.exceptions.VerificationCodeRateLimitException;
import com.example.myauth.repositories.UserRepository;
import com.example.myauth.utils.RandomVerificationCode;

@Service
public class VerificationService {

    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(10);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final Duration RESEND_WINDOW = Duration.ofHours(1);
    private static final int MAX_RESENDS_PER_HOUR = 5;
    private static final int MAX_REQUESTS_PER_IP_PER_HOUR = 20;
    private static final int MAX_VERIFICATION_ATTEMPTS = 5;
    private static final DefaultRedisScript<Long> INCREMENT_WITH_TTL = new DefaultRedisScript<>(
            "local current = redis.call('INCR', KEYS[1]); " +
                    "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end; return current;",
            Long.class);
    private static final DefaultRedisScript<Long> CONSUME_CODE = new DefaultRedisScript<>(
            "if redis.call('GET', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('DEL', KEYS[1]); end; return 0;",
            Long.class);

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

    public VerificationService(UserRepository userRepository, RedisTemplate<String, String> redisTemplate,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }

    private String activeVerificationKey(String email) {
        return "verification:active:" + email;
    }

    public void createVerificationCode(String email, String code) {
        try {
            String activeKey = activeVerificationKey(email);
            redisTemplate.opsForValue().set(activeKey, code, VERIFICATION_CODE_TTL);
        } catch (DataAccessException ex) {
            throw new RedisOperationException("Unable to store verification code.", ex);
        }
    }

    // Tăng 1 check rate limit, nếu vượt quá thì throw exception
    private void enforceHourlyLimit(String key, int maxRequests) {
        Long requests = redisTemplate.execute(INCREMENT_WITH_TTL, List.of(key),
                String.valueOf(RESEND_WINDOW.toSeconds()));
        if (requests != null && requests > maxRequests) {
            Long remaining = redisTemplate.getExpire(key);
            throw new VerificationCodeRateLimitException(Math.max(1, remaining == null ? 3600 : remaining));
        }
    }

    private void applyRateLimits(String email, String clientAddress) {
        try {
            enforceHourlyLimit("verification:resend:hourly:" + email, MAX_RESENDS_PER_HOUR);
            enforceHourlyLimit("verification:resend:ip:" + (clientAddress == null ? "unknown" : clientAddress),
                    MAX_REQUESTS_PER_IP_PER_HOUR);
            String cooldownKey = "verification:resend:cooldown:" + email;
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
                    cooldownKey, "1", RESEND_COOLDOWN);

            if (Boolean.FALSE.equals(acquired)) {
                Long remaining = redisTemplate.getExpire(cooldownKey);
                throw new VerificationCodeRateLimitException(Math.max(1, remaining == null ? 60 : remaining));
            }

        } catch (VerificationCodeRateLimitException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new RedisOperationException("Unable to apply verification-code rate limits.", ex);
        }
    }

    public boolean validateVerificationCode(String email, String code) {
        String normalizedEmail = normalizeEmail(email);
        try {
            String key = activeVerificationKey(normalizedEmail);
            Long consumed = redisTemplate.execute(CONSUME_CODE, List.of(key), code);
            if (consumed == null || consumed == 0) {
                enforceHourlyLimit("verification:attempts:" + normalizedEmail, MAX_VERIFICATION_ATTEMPTS);
                return false;
            }

            User user = userRepository.findByEmail(normalizedEmail).orElse(null);
            if (user == null) {
                return false;
            }
            user.setEnabled(true);
            userRepository.save(user);
            redisTemplate.delete("verification:attempts:" + normalizedEmail);
            return true;
        } catch (DataAccessException ex) {
            throw new RedisOperationException("Unable to validate verification code.", ex);
        }
    }

    public void resendVerificationCode(String email, String clientAddress) {
        String normalizedEmail = normalizeEmail(email);
        applyRateLimits(normalizedEmail, clientAddress);
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user == null || user.isEnabled()) {
            return;
        }

        String code = RandomVerificationCode.generateCode();
        createVerificationCode(normalizedEmail, code);
        emailService.sendVerificationEmail(normalizedEmail, user.getFullName(), code);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

}
