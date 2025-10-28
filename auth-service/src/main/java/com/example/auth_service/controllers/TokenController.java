    package com.example.auth_service.controllers;

    import com.example.auth_service.dtos.LoginResponse;
    import com.example.auth_service.dtos.LogoutDto;
    import com.example.auth_service.dtos.MessageResponse;
    import com.example.auth_service.models.User;
    import com.example.auth_service.services.AuthService;
    import com.example.auth_service.services.JwtService;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    public class TokenController {
        private final JwtService jwtService;
        private final AuthService authenticationService;

        public TokenController(JwtService jwtService, AuthService authenticationService) {
            this.jwtService = jwtService;
            this.authenticationService = authenticationService;
        }

        @PostMapping("/logout")
        public ResponseEntity<MessageResponse> logout(@RequestHeader("Authorization") String authHeader, @RequestBody LogoutDto logoutDto) {
            String token = authHeader.replace("Bearer ", "");
            jwtService.addToBlacklist(token);
            jwtService.addToBlacklist(logoutDto.getRefreshToken());
            MessageResponse messageResponse = new MessageResponse("Logout successfully");
            return ResponseEntity.ok(messageResponse);
        }

        @PostMapping("/refresh")
        public ResponseEntity<LoginResponse> refresh(@RequestHeader("Authorization") String authHeader) {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUsername(token);
            User user = authenticationService.loadUserByUsername(username);

            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(jwtToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setAccessExpiresIn(jwtService.getExpirationTime());

            jwtService.addToBlacklist(token);
            return ResponseEntity.ok(loginResponse);
        }
    }
