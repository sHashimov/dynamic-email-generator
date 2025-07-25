package com.emailgen.service;

import com.emailgen.dto.EmailResponseDTO;
import com.emailgen.dto.EmailResultItem;
import com.emailgen.util.ExpressionEvaluator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EmailGeneratorService {

    public EmailResponseDTO generateEmail(Map<String, String> inputs, String expression) {
        String evaluatedEmail = ExpressionEvaluator.evaluate(expression, inputs);
        return new EmailResponseDTO(List.of(new EmailResultItem(evaluatedEmail, evaluatedEmail)));
    }
}
