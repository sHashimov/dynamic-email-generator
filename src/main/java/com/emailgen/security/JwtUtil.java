package com.emailgen.security;

import com.emailgen.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, List<String> roles) {
        log.debug("Generating token for user: {}", username);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration());

        String token = Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        log.debug("Token generated successfully for user: {}", username);
        return token;
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

            List<String> roles = claims.get("roles", List.class);
            log.debug("Extracted roles from token: {}", roles);
            return roles;
        } catch (JwtException e) {
            log.warn("Failed to extract roles from token: {}", e.getMessage());
            throw e;
        }
    }

    public String extractUsername(String token) {
        try {
            String username = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

            log.debug("Extracted username from token: {}", username);
            return username;
        } catch (JwtException e) {
            log.warn("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token, String expectedUsername) {
        try {
            String actualUsername = extractUsername(token);
            boolean isValid = actualUsername.equals(expectedUsername);
            log.debug("Token validation result for user {}: {}", expectedUsername, isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid token for user {}: {}", expectedUsername, ex.getMessage());
            return false;
        }
    }
}
