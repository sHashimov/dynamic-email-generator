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
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
            "user",
            null,
            List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
            )
        );
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);
        when(jwtUtil.generateToken("user", List.of("USER", "ADMIN")))
            .thenReturn("mocked.jwt.token");

        List<String> expectedRoles = List.of("USER", "ADMIN");
        when(jwtUtil.generateToken("user", expectedRoles)).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("user", expectedRoles);
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

    private GrantedAuthority mockAuthority(String role) {
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn(role);
        return authority;
    }
}
