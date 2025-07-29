package com.emailgen.service.email;

import com.emailgen.dto.email.EmailResponseDTO;
import com.emailgen.dto.email.EmailResultItem;
import com.emailgen.util.ExpressionEvaluator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EmailGeneratorService {

    public EmailResponseDTO generateEmails(Map<String, String> inputs, List<String> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            throw new IllegalArgumentException("At least one expression is required.");
        }

        List<EmailResultItem> results = ExpressionEvaluator.evaluateAll(expressions, inputs)
            .stream()
            .map(email -> new EmailResultItem(email, email))
            .toList();

        return new EmailResponseDTO(results);
    }
}

