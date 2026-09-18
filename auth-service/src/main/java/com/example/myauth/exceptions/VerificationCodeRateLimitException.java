package com.example.myauth.exceptions;

public class VerificationCodeRateLimitException extends RuntimeException {
    private final long retryAfterSeconds;

    public VerificationCodeRateLimitException(long retryAfterSeconds) {
        super("Please wait before requesting another verification code.");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
