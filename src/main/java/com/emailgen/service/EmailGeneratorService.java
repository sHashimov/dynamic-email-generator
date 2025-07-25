package com.emailgen.service;

import com.emailgen.dto.EmailResponseDTO;
import com.emailgen.dto.EmailResultItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmailGeneratorService {

    public EmailResponseDTO generateEmail(Map<String, String> inputs, String expression) {
        // Placeholder response
        String email = "placeholder@email.com";
        return new EmailResponseDTO(
            List.of(new EmailResultItem(email, email))
        );
    }
}
