package com.example.myauth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerificationRequestDto requestDto) {
        String email = requestDto.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        String code = requestDto.getCode();
        boolean isValid = verificationService.validateVerificationCode(email, code);
        if (isValid) {
            return ResponseEntity.ok("Your account has been verified successfully.");
        } else {
            return ResponseEntity.badRequest()
                    .body("Invalid verification code.");
        }
    }

    @PostMapping("/resend-code")
    public ResponseEntity<String> resendVerificationCode(@Valid @RequestBody ResendCodeRequestDto requestDto) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String clientAddress = attributes == null ? "unknown" : attributes.getRequest().getRemoteAddr();
        verificationService.resendVerificationCode(requestDto.getEmail(), clientAddress);
        return ResponseEntity.accepted()
                .body("If the account exists and is not verified, a verification code will be sent.");
    }

}
