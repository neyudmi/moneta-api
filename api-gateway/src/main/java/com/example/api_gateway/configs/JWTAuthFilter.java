package com.example.api_gateway.configs;

import com.example.api_gateway.dtos.UserResponse;
import com.example.api_gateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class JWTAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final WebClient webClient;

    public JWTAuthFilter(JwtUtil jwtUtil, WebClient.Builder webClientBuilder) {
        this.jwtUtil = jwtUtil;
        this.webClient = webClientBuilder.baseUrl("http://auth-service:8080").build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        if (pathMatcher.match("/auth/**", path)) {
            System.out.println("JWTAuthFilter: Skipping authentication for path: " + path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            String userId = jwtUtil.extractUserId(token).toString();
            String username = jwtUtil.extractUsername(token);

            if (!jwtUtil.isTokenValid(token) || jwtUtil.isTokenBlacklisted(token)) {
                return unauthorized(exchange, "Invalid token");
            }

            return webClient.get()
                    .uri("/users/{userId}/password-last-changed", userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(LocalDateTime.class)
                    .flatMap(passwordLastChanged -> {
                        // 4. Compare with token's timestamp
                        LocalDateTime tokenPasswordChangeAt = jwtUtil.getPasswordChangeAt(token);

                        LocalDateTime tokenTime = tokenPasswordChangeAt.truncatedTo(ChronoUnit.MILLIS);
                        LocalDateTime userTime = passwordLastChanged.truncatedTo(ChronoUnit.MILLIS);

                        if (tokenTime.isBefore(userTime)) {
                            return unauthorized(exchange, "Token invalidated due to password change");
                        }

                        // 5. If valid, forward request with user headers
                        return chain.filter(
                                exchange.mutate()
                                        .request(builder -> builder
                                                .header("X-User-Id", jwtUtil.extractUserId(token).toString())
                                                .header("X-User-Email", username)
                                        )
                                        .build()
                        );
                    })
                    .onErrorResume(e -> unauthorized(exchange, "Authentication failed: " + e.getMessage()));
        } catch (Exception e) {
            return unauthorized(exchange, "Token parsing error: " + e.getMessage());
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String reason) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("error", reason);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
} 
