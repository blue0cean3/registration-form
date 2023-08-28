package com.vietch02.registrationform.security.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// create constructor using final field
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // request and response are from us. provice by response
    // filterChain: contain list of other filter need to execute

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;
        // check jwtToken
        // authorization always start with Bearer: give token to bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // extract token from authHeader
        // B e a r e r and the space is 7 characters
        jwtToken = authHeader.substring(7);
        // after check jwtToken, we need to call user service to check if we have user
        // or not. Need to call jwtService first to check to extract username
        // TODO extract user email from jwt token;
        userEmail = jwtService.extractUsername(jwtToken);
    }

}
