package com.example.api_gateway.configs;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.example.api_gateway.services.JwtService;
import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        if (path.startsWith("/swagger-docs/")
                || path.startsWith("/swagger-ui/")
                || path.equals("/swagger-ui.html")) {
            return chain.filter(exchange);
        }
        if (!requiresAuthentication(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            return unauthorized(exchange);
        }

        String jwt = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(jwt)) {
                return unauthorized(exchange);
            }

            String tokenId = jwtService.extractTokenId(jwt);

            if (jwtService.isBlacklisted(tokenId)) {
                return unauthorized(exchange);
            }

            return chain.filter(exchange);

        } catch (JwtException | IllegalArgumentException e) {
            return unauthorized(exchange);

        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    private boolean requiresAuthentication(String path) {

        if (path.equals("/auth/logout")) {
            return true;
        }

        if (path.startsWith("/auth/")) {
            return false;
        }

        return true;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}