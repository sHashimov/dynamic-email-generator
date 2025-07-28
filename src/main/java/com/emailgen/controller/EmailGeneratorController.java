package com.emailgen.controller;

import com.emailgen.dto.EmailResponseDTO;
import com.emailgen.service.EmailGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class EmailGeneratorController {

    private final EmailGeneratorService service;

    @Operation(
        summary = "Generate dynamic email content",
        description = "Evaluates the given expression using provided input values and returns the resulting email string.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful email generation",
                content = @Content(schema = @Schema(implementation = EmailResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or missing parameters"),
            @ApiResponse(responseCode = "500", description = "Server error")
        }
    )
    @GetMapping("/generate-email")
    public EmailResponseDTO generateEmail(@RequestParam Map<String, String> allParams) {
        String expression = allParams.remove("expression");

        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("The 'expression' parameter is required.");
        }

        for (String key : allParams.keySet()) {
            if (!key.matches("input\\d+")) {
                throw new IllegalArgumentException("Invalid input key: " + key);
            }
        }

        return service.generateEmail(allParams, expression);
    }

}
