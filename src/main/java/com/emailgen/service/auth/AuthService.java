package com.emailgen.service.auth;

import static com.emailgen.security.SecurityConstants.ROLE_PREFIX;

import com.emailgen.dto.auth.AuthRequest;
import com.emailgen.dto.auth.AuthResponse;
import com.emailgen.exception.InvalidCredentialsException;
import com.emailgen.security.JwtUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
        String username = request.getUsername();
        String password = request.getPassword();

        try {
            Authentication authentication = authenticateUser(username, password);
            log.info("User '{}' authenticated successfully", username);

            List<String> roles = extractRoles(authentication);
            String token = jwtUtil.generateToken(username, roles);

            return new AuthResponse(token);
        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed for user '{}'", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    private Authentication authenticateUser(String username, String password) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    private List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(role -> role.replace(ROLE_PREFIX, ""))
            .toList();
    }

}