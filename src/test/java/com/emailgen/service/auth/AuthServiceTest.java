package com.emailgen.service.auth;

import com.emailgen.dto.auth.AuthRequest;
import com.emailgen.dto.auth.AuthResponse;
import com.emailgen.exception.InvalidCredentialsException;
import com.emailgen.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(authenticationManager, jwtUtil);
    }

    @Test
    void authenticate_shouldReturnToken_whenCredentialsAreValid() {
        AuthRequest request = new AuthRequest("user", "pass");
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);
        when(mockAuth.getName()).thenReturn("user");
        when(jwtUtil.generateToken("user")).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("user");
    }

    @Test
    void authenticate_shouldThrowInvalidCredentialsException_whenBadCredentials() {
        AuthRequest request = new AuthRequest("user", "wrongpass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () ->
            authService.authenticate(request)
        );

        assertEquals("Invalid username or password", ex.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtil);
    }
}
