package com.example.myauth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.example.myauth.dtos.LoginRequestDto;
import com.example.myauth.dtos.LoginResponseDto;
import com.example.myauth.dtos.RegisterRequestDto;
import com.example.myauth.dtos.ResendCodeRequestDto;
import com.example.myauth.dtos.VerificationRequestDto;
import com.example.myauth.dtos.ForgotPasswordRequestDto;
import com.example.myauth.dtos.ResetPasswordRequestDto;
import com.example.myauth.entities.User;
import com.example.myauth.services.AuthService;
import com.example.myauth.services.JwtService;
import com.example.myauth.services.VerificationService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final VerificationService verificationService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, VerificationService verificationService,
            JwtService jwtService) {
        this.authService = authService;
        this.verificationService = verificationService;
        this.jwtService = jwtService;

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto userDto) {
        User authenticatedUser = authService.login(userDto);
        String accessToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

        LoginResponseDto responseDto = new LoginResponseDto(
                authenticatedUser.getId(),
                accessToken,
                refreshToken,
                jwtService.getJwtExpiration());

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUserAccount(@Valid @RequestBody RegisterRequestDto userDto) {
        authService.register(userDto);
        return ResponseEntity.status(201)
                .body("User registered successfully. Please check your email for verification.");
    }

    @PostMapping("/active")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerificationRequestDto requestDto) {
        String email = requestDto.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        String code = requestDto.getCode();
        boolean isValid = authService.activateUser(email, code);
        if (isValid) {
            return ResponseEntity.ok("Your account has been verified successfully.");
        } else {
            return ResponseEntity.badRequest()
                    .body("Invalid verification code.");
        }
    }

    @PostMapping("/resend-code")
    public ResponseEntity<String> resendVerificationCode(@Valid @RequestBody ResendCodeRequestDto requestDto) {
        authService.resendCode(requestDto.getEmail());
        return ResponseEntity.accepted()
                .body("Code resent successfully. Please check your email for the new verification code.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto requestDto) {
        authService.sendPasswordResetCode(requestDto.getEmail());
        return ResponseEntity.accepted()
                .body("If the account exists, a password reset code will be sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto) {
        authService.resetPassword(
                requestDto.getEmail(),
                requestDto.getCode(),
                requestDto.getNewPassword());
        return ResponseEntity.ok("Password reset successfully. Please log in again.");
    }

}
