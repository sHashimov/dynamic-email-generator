package com.emailgen.service.email;

import com.emailgen.dto.email.EmailResponseDTO;
import com.emailgen.dto.email.EmailResultItem;
import com.emailgen.exception.ExpressionEvaluationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmailGeneratorServiceTest {

    private final EmailGeneratorService service = new EmailGeneratorService();

    @Test
    @DisplayName("Generates correct email from valid single expression")
    void testGenerateEmail_Success() {
        Map<String, String> inputs = Map.of(
            "input1", "Han",
            "input2", "Solo",
            "input3", "galaxy"
        );
        List<String> expressions = List.of(
            "{input1|first:1|lower}~'.'~{input2|last:3|lower}~'@'~{input3|all|lower}~'.com'"
        );

        EmailResponseDTO response = service.generateEmails(inputs, expressions);
        assertNotNull(response);
        assertEquals(1, response.getData().size());

        EmailResultItem email = response.getData().get(0);
        assertEquals("h.olo@galaxy.com", email.getValue());
    }

    @Test
    @DisplayName("Generates emails from multiple expressions")
    void testGenerateEmail_MultipleExpressions() {
        Map<String, String> inputs = Map.of(
            "input1", "Jane",
            "input2", "Doe"
        );
        List<String> expressions = List.of(
            "{input1|first:1|lower}~'.'~{input2|all|lower}~'@example.com'",
            "{input1|all|lower}~'.'~{input2|first:2|lower}~'@example.com'"
        );

        EmailResponseDTO response = service.generateEmails(inputs, expressions);
        assertNotNull(response);
        assertEquals(2, response.getData().size());

        assertEquals("j.doe@example.com", response.getData().get(0).getValue());
        assertEquals("jane.do@example.com", response.getData().get(1).getValue());
    }

    @Test
    @DisplayName("Throws exception when input is missing")
    void testGenerateEmail_MissingInput() {
        Map<String, String> inputs = Map.of();
        List<String> expressions = List.of("{input1|first:2}");

        ExpressionEvaluationException exception = assertThrows(
            ExpressionEvaluationException.class,
            () -> service.generateEmails(inputs, expressions)
        );
        assertEquals("Missing input for key: input1", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception for unknown function in expression")
    void testGenerateEmail_ThrowsOnUnknownFunction() {
        Map<String, String> inputs = Map.of("input1", "Data");
        List<String> expressions = List.of("{input1|someUnknownFunc:3}");

        assertThrows(ExpressionEvaluationException.class, () ->
            service.generateEmails(inputs, expressions));
    }

    @Test
    @DisplayName("Throws exception for malformed expression")
    void testGenerateEmail_ThrowsOnMalformedFunction() {
        Map<String, String> inputs = Map.of("input1", "Malform");
        List<String> expressions = List.of("{input1|first:2");

        assertThrows(ExpressionEvaluationException.class, () ->
            service.generateEmails(inputs, expressions));
    }

    @Test
    @DisplayName("Throws exception when expression list is null")
    void testNullExpressionListThrows() {
        Map<String, String> inputs = Map.of("input1", "John");

        assertThrows(IllegalArgumentException.class, () ->
            service.generateEmails(inputs, null));
    }

    @Test
    @DisplayName("Throws exception when expression list is empty")
    void testEmptyExpressionListThrows() {
        Map<String, String> inputs = Map.of("input1", "John");

        assertThrows(IllegalArgumentException.class, () ->
            service.generateEmails(inputs, List.of()));
    }

    @Test
    @DisplayName("Generates multiple emails from multiple valid expressions")
    void testGenerateEmails_Success() {
        Map<String, String> inputs = Map.of(
            "input1", "Han",
            "input2", "Solo",
            "input3", "galaxy"
        );
        List<String> expressions = List.of(
            "{input1|first:1|lower}~'.'~{input2|last:3|lower}~'@'~{input3|all|lower}~'.com'",
            "{input1|upper}~'.'~{input2|upper}~'@EXAMPLE.COM'"
        );

        EmailResponseDTO response = service.generateEmails(inputs, expressions);

        assertNotNull(response);
        assertEquals(2, response.getData().size());

        assertEquals("h.olo@galaxy.com", response.getData().get(0).getValue());
        assertEquals("HAN.SOLO@EXAMPLE.COM", response.getData().get(1).getValue());
    }
}
