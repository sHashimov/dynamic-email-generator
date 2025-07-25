package com.emailgen.service;

import com.emailgen.dto.EmailResponseDTO;
import com.emailgen.dto.EmailResultItem;
import com.emailgen.exception.ExpressionEvaluationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmailGeneratorServiceTest {

    private final EmailGeneratorService service = new EmailGeneratorService();

    @Test
    @DisplayName("Generates correct email from valid expression")
    void testGenerateEmail_Success() {
        // Arrange
        Map<String, String> inputs = Map.of(
            "input1", "Han",
            "input2", "Solo",
            "input3", "galaxy"
        );
        String expression = "input1.firstChars(1).lower()~'.'~input2.lastChars(3).lower()~'@'~input3.allChars().lower()~'.com'";

        // Act
        EmailResponseDTO response = service.generateEmail(inputs, expression);
        EmailResultItem email = response.getData().get(0);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals("h.olo@galaxy.com", email.getValue());
    }

    @Test
    @DisplayName("Throws exception when input is missing")
    void testGenerateEmail_MissingInput() {
        // Arrange
        Map<String, String> inputs = Map.of();  // missing input1
        String expression = "input1.firstChars(2)";

        // Act & Assert
        ExpressionEvaluationException exception = assertThrows(
            ExpressionEvaluationException.class,
            () -> service.generateEmail(inputs, expression)
        );
        assertEquals("Missing input for key: input1", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception for unknown function in expression")
    void testGenerateEmail_ThrowsOnUnknownFunction() {
        // Arrange
        Map<String, String> inputs = Map.of("input1", "Data");
        String expression = "input1.someUnknownFunc(3)";

        // Act & Assert
        assertThrows(ExpressionEvaluationException.class, () ->
            service.generateEmail(inputs, expression));
    }

    @Test
    @DisplayName("Throws exception for malformed expression")
    void testGenerateEmail_ThrowsOnMalformedFunction() {
        // Arrange
        Map<String, String> inputs = Map.of("input1", "Malform");
        String expression = "input1.firstChars(2"; // missing closing paren

        // Act & Assert
        assertThrows(ExpressionEvaluationException.class, () ->
            service.generateEmail(inputs, expression));
    }

    @Test
    @DisplayName("Throws exception when expression is null")
    void testNullExpressionThrows() {
        // Arrange
        Map<String, String> inputs = Map.of("input1", "John");

        // Act & Assert
        assertThrows(ExpressionEvaluationException.class, () ->
            service.generateEmail(inputs, null)
        );
    }

    @Test
    @DisplayName("Throws exception when expression is blank")
    void testBlankExpressionThrows() {
        // Arrange
        Map<String, String> inputs = Map.of("input1", "John");

        // Act & Assert
        assertThrows(ExpressionEvaluationException.class, () ->
            service.generateEmail(inputs, "  ")
        );
    }

}

