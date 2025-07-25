package com.emailgen.controller;

import com.emailgen.dto.EmailResponseDTO;
import com.emailgen.service.EmailGeneratorService;
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
