package com.emailgen.service.auth;

import com.emailgen.dto.auth.AuthRequest;
import com.emailgen.dto.auth.AuthResponse;
import com.emailgen.exception.InvalidCredentialsException;
import com.emailgen.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            log.info("User '{}' authenticated successfully", request.getUsername());

            String token = jwtUtil.generateToken(authentication.getName());
            return new AuthResponse(token);
        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed for user '{}'", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
}