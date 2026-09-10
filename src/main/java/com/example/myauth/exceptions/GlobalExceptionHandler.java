package com.example.myauth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import io.jsonwebtoken.JwtException;

import jakarta.mail.MessagingException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Invalid request.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body("Invalid request.");
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("An account with this email already exists.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwt(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<String> handleMessagingException(MessagingException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error occurred while sending email: " + ex.getMessage());
    }

    @ExceptionHandler(VerificationCodeRateLimitException.class)
    public ResponseEntity<String> handleRateLimit(VerificationCodeRateLimitException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getRetryAfterSeconds()))
                .body(ex.getMessage());
    }

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<String> handleRedisFailure(RedisOperationException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Verification service is temporarily unavailable.");
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<String> handleEmailFailure(EmailSendingException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Unable to send verification email. Please try again later.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred.");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPassword(InvalidPasswordException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}