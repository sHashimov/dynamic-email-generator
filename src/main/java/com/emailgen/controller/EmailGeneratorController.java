package com.emailgen.controller;

import com.emailgen.dto.EmailResponseDTO;
import com.emailgen.service.EmailGeneratorService;
import com.emailgen.util.ExpressionEvaluationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class EmailGeneratorController {

    private final EmailGeneratorService service;

    @GetMapping("/generate-email")
    public ResponseEntity<EmailResponseDTO> generateEmail(@RequestParam Map<String, String> allParams) {
        String expression = allParams.remove("expression");

        if (expression == null || expression.isBlank()) {
            throw new ExpressionEvaluationException("Missing or empty 'expression' parameter");
        }

        return ResponseEntity.ok(service.generateEmail(allParams, expression));
    }

}
