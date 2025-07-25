package com.emailgen.controller;

import com.emailgen.dto.EmailResponseDTO;
import com.emailgen.service.EmailGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/generate-email")
@RequiredArgsConstructor
public class EmailGeneratorController {

    private final EmailGeneratorService service;

    @GetMapping
    public EmailResponseDTO generateEmail(@RequestParam Map<String, String> allParams) {
        String expression = allParams.remove("expression");
        return service.generateEmail(allParams, expression);
    }
}
