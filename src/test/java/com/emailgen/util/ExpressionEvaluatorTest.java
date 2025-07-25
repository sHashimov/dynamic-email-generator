package com.emailgen.util;

import com.emailgen.exception.ExpressionEvaluationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    @Test
    @DisplayName("Evaluates firstChars(n) correctly")
    void testFirstChars() {
        // Arrange
        String expression = "input1.firstChars(2)";
        Map<String, String> inputs = Map.of("input1", "Jonathan");

        // Act
        String result = ExpressionEvaluator.evaluate(expression, inputs);

        // Assert
        assertEquals("Jo", result);
    }

    @Test
    @DisplayName("Evaluates lastChars(n) correctly")
    void testLastChars() {
        // Arrange
        String expression = "input2.lastChars(3)";
        Map<String, String> inputs = Map.of("input2", "Anderson");

        // Act
        String result = ExpressionEvaluator.evaluate(expression, inputs);

        // Assert
        assertEquals("son", result);
    }

    @Test
    @DisplayName("Evaluates allChars() correctly")
    void testAllChars() {
        // Arrange
        String expression = "input3.allChars()";
        Map<String, String> inputs = Map.of("input3", "Hello");

        // Act
        String result = ExpressionEvaluator.evaluate(expression, inputs);

        // Assert
        assertEquals("Hello", result);
    }

    @Test
    @DisplayName("Throws exception for unknown function")
    void testThrowsOnUnknownFunction() {
        // Arrange
        String expression = "input1.someUnknownFunc(5)";
        Map<String, String> inputs = Map.of("input1", "Test");

        // Act & Assert
        Exception exception = assertThrows(ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs));
        assertTrue(exception.getMessage().toLowerCase().contains("unknown"));
    }

    @Test
    @DisplayName("Throws exception for missing input")
    void testMissingInput() {
        // Arrange
        String expression = "input999.firstChars(2)";
        Map<String, String> inputs = Map.of();

        // Act + Assert
        ExpressionEvaluationException exception = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs)
        );

        assertEquals("Missing input for key: input999", exception.getMessage());
    }

    @Test
    @DisplayName("Supports quoted strings and glue")
    void testQuotedLiterals() {
        // Arrange
        String expression = "\"prefix_\"~input1.allChars()~\"_suffix\"";
        Map<String, String> inputs = Map.of("input1", "core");

        // Act
        String result = ExpressionEvaluator.evaluate(expression, inputs);

        // Assert
        assertEquals("prefix_core_suffix", result);
    }

    @Test
    @DisplayName("Supports chaining: firstChars + lower")
    void testChainedFunctionCalls() {
        // Arrange
        String expression = "input1.firstChars(2).lower()";
        Map<String, String> inputs = Map.of("input1", "Han");

        // Act
        String result = ExpressionEvaluator.evaluate(expression, inputs);

        // Assert
        assertEquals("ha", result);
    }

    @Test
    @DisplayName("Evaluates full email generation expression")
    void testComplexEmailExpression() {
        // Arrange
        String expression = "input1.firstChars(1).lower()~'.'~input2.lastChars(3).lower()~'@'~input3.allChars().lower()~'.com'";
        Map<String, String> inputs = Map.of(
            "input1", "Han",
            "input2", "Solo",
            "input3", "Galaxy"
        );

        // Act
        String result = ExpressionEvaluator.evaluate(expression, inputs);

        // Assert
        assertEquals("h.olo@galaxy.com", result);
    }

    @Test
    @DisplayName("Throws exception for malformed function call")
    void testMalformedFunctionCall() {
        // Arrange
        String expression = "input1.firstChars(2";
        Map<String, String> inputs = Map.of("input1", "Test");

        // Act & Assert
        assertThrows(ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs));
    }

    @Test
    @DisplayName("Throws exception for non-numeric argument")
    void testNonNumericArgument() {
        // Arrange
        String expression = "input1.firstChars(x)";
        Map<String, String> inputs = Map.of("input1", "Hello");

        // Act & Assert
        Exception exception = assertThrows(ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs));
        String message = exception.getMessage();
        assertTrue(message.contains("Malformed function call") || message.contains("Invalid argument"));
    }

    @Test
    @DisplayName("Throws exception when expression is null")
    void testNullExpressionThrows() {
        // Act + Assert
        ExpressionEvaluationException exception = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(null, Map.of("input1", "John"))
        );

        assertEquals("Missing or empty expression", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception when expression is blank")
    void testBlankExpressionThrows() {
        // Act + Assert
        ExpressionEvaluationException exception = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate("   ", Map.of("input1", "John"))
        );

        assertEquals("Missing or empty expression", exception.getMessage());
    }
}
