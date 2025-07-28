package com.emailgen.controller;

import com.emailgen.dto.EmailGenerationRequestDTO;
import com.emailgen.dto.EmailResponseDTO;
import com.emailgen.service.EmailGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class EmailGeneratorController {

    private final EmailGeneratorService service;

    @Operation(
        summary = "Generate one or more dynamic email contents",
        description = "Evaluates the given expressions using provided input values and returns the resulting list of email strings.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful email generation",
                content = @Content(schema = @Schema(implementation = EmailResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or missing parameters"),
            @ApiResponse(responseCode = "422", description = "Validation failed for input parameters"),
            @ApiResponse(responseCode = "500", description = "Server error")
        }
    )
    @PostMapping("/generate-email")
    public EmailResponseDTO generateEmails(
        @Parameter(description = "Email generation request containing inputs and expressions")
        @Valid @RequestBody EmailGenerationRequestDTO request) {
        for (String key : request.getInputs().keySet()) {
            if (!key.matches("input\\d+")) {
                throw new IllegalArgumentException("Invalid input key: " + key);
            }
        }
        if (request.getExpressions() == null || request.getExpressions().isEmpty()) {
            throw new IllegalArgumentException("At least one expression is required.");
        }

        return service.generateEmails(request.getInputs(), request.getExpressions());
    }


}
