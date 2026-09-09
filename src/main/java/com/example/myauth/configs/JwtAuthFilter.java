package com.example.myauth.configs;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.myauth.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public JwtAuthFilter(UserDetailsService userDetailsService, JwtService jwtService,
            HandlerExceptionResolver handlerExceptionResolver) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            final String jwt = authHeader.substring(7); // lấy jwt
            final String tokenType = jwtService.extractTokenType(jwt); // lấy loại token

            if (!"access".equals(tokenType)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String userEmail = jwtService.extractUsername(jwt); // lấy email từ jwt

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); // lấy thông tin user
                                                                                                 // từ email

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            logger.error("JWT filter exception", exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
            return;
        }
    }
}
