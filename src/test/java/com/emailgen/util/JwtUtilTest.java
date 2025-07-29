package com.emailgen.util;

import com.emailgen.config.JwtProperties;
import com.emailgen.security.JwtUtil;
import com.emailgen.security.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET_KEY = "E8nDd5XlKr7WpG9sQyZmJ2vTxUeHaKb3";

    private JwtUtil jwtUtil;
    private final String username = "testuser";
    private final List<String> roles = List.of(Roles.USER);

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret(SECRET_KEY);
        jwtProperties.setExpiration(3600000); // 1 hour
        jwtUtil = new JwtUtil(jwtProperties);
    }

    @Test
    void generateToken_shouldContainCorrectUsername() {
        String token = jwtUtil.generateToken(username, roles);
        String extracted = jwtUtil.extractUsername(token);
        assertEquals(username, extracted);
    }

    @Test
    void extractUsername_shouldReturnExpectedUsername() {
        String token = jwtUtil.generateToken(username, roles);
        String result = jwtUtil.extractUsername(token);
        assertEquals(username, result);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken(username, roles);
        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        String token = jwtUtil.generateToken("anotheruser", roles);
        assertFalse(jwtUtil.validateToken(token, username));
    }

    @Test
    void validateToken_shouldReturnFalseForMalformedToken() {
        String malformedToken = "this.is.not.valid.jwt";
        assertFalse(jwtUtil.validateToken(malformedToken, username));
    }
}
