package com.emailgen.controller.auth;

import com.emailgen.dto.auth.AuthRequest;
import com.emailgen.dto.auth.AuthResponse;
import com.emailgen.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Authenticate user and return JWT token",
        description =
            "Authenticates a user using their credentials (username and password) and returns a JWT token on successful login."
                + "The token can be used for accessing secured endpoints.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Missing or malformed credentials"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "500", description = "Server error")
        }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        AuthResponse response = authService.authenticate(request);
        log.info("Login successful for user: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }
}
