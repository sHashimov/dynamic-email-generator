package com.emailgen.service.email;

import com.emailgen.dto.email.EmailResponseDTO;
import com.emailgen.dto.email.EmailResultItem;
import com.emailgen.util.ExpressionEvaluator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailGeneratorService {
    private static final Logger log = LoggerFactory.getLogger(EmailGeneratorService.class);

    public EmailResponseDTO generateEmails(Map<String, String> inputs, List<String> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            throw new IllegalArgumentException("At least one expression is required.");
        }
        log.debug("Evaluating {} expressions with {} input(s)", expressions.size(), inputs.size());

        List<EmailResultItem> results = ExpressionEvaluator.evaluateAll(expressions, inputs)
            .stream()
            .map(email -> new EmailResultItem(email, email))
            .toList();

        log.info("Successfully generated {} email(s)", results.size());
        return new EmailResponseDTO(results);
    }
}

