package com.example.myauth.utils;

import java.security.SecureRandom;

public class RandomVerificationCode {

    private static final SecureRandom random = new SecureRandom();

    public static String generateCode() {
        return String.format("%06d", random.nextInt(1_000_000));
    }
}
