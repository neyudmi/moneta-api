package com.example.myauth.services;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.myauth.exceptions.RedisOperationException;
import com.example.myauth.exceptions.VerificationCodeRateLimitException;
import com.example.myauth.entities.User;
import com.example.myauth.repositories.UserRepository;
import com.example.myauth.utils.RandomVerificationCode;

@Service
public class VerificationService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(10);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final DefaultRedisScript<Long> CONSUME_CODE = new DefaultRedisScript<>(
            "if redis.call('GET', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('DEL', KEYS[1]); end; return 0;",
            Long.class);

    public VerificationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // verifcation:active:<email> -> <code> with TTL 10 minutes
    private void createCode(String email, String code) {
        try {
            String key = "verification:active:" + email;
            redisTemplate.opsForValue().set(key, code, VERIFICATION_CODE_TTL);
        } catch (DataAccessException ex) {
            throw new RedisOperationException("Unable to store verification code.", ex);
        }
    }

    private void applyResendCooldown(String email) {
        try {
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

    public String issueCode(String email) {
        String code = RandomVerificationCode.generateCode();

        applyResendCooldown(email);
        createCode(email, code);
        return code;
    }

    public boolean consumeCode(String email, String code) {
        try {
            String key = "verification:active:" + email;
            Long consumed = redisTemplate.execute(CONSUME_CODE, List.of(key), code);
            if (consumed == null || consumed == 0) {
                return false;
            }
            return true;
        } catch (DataAccessException ex) {
            throw new RedisOperationException("Unable to validate verification code.", ex);
        }
    }

}
