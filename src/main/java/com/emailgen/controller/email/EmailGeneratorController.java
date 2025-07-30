package com.emailgen.controller.email;

import com.emailgen.dto.email.EmailGenerationRequestDTO;
import com.emailgen.dto.email.EmailResponseDTO;
import com.emailgen.service.email.EmailGeneratorService;
import com.emailgen.util.InputParamResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class EmailGeneratorController {

    private static final Logger log = LoggerFactory.getLogger(EmailGeneratorController.class);

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

        EmailResponseDTO response = service.generateEmails(request.getInputs(), request.getExpressions());
        log.info("Generated {} email(s) successfully", response.getData().size());
        return response;
    }

    @Operation(
        summary = "Generate a dynamic email content using query parameters",
        description = "Evaluates a single expression using input parameters provided either as individual query params (e.g., input1, input2, etc.) or as a fallback JSON string via `allParams`.",
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
    @GetMapping("/generate-email")
    public EmailResponseDTO generateEmailsViaQuery(
        @RequestParam(name = "expression", required = false) String expression,
        @RequestParam Map<String, String> inputParams,
        @RequestParam(name = "allParams", required = false) String allParamsJson
    ) {
        log.info("Received GET request to generate email with expression: {}", expression);

        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("The 'expression' parameter is required.");
        }

        Map<String, String> resolvedInputs = InputParamResolver.resolveInputs(inputParams, allParamsJson);
        log.debug("Resolved {} input parameters", resolvedInputs.size());

        return service.generateEmails(resolvedInputs, List.of(expression));
    }
}
