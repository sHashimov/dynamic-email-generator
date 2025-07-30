package com.emailgen.util;

import com.emailgen.exception.ExpressionEvaluationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    @Test
    @DisplayName("Evaluates first:n correctly")
    void testFirstChars() {
        String expression = "{input1|first:2}";
        Map<String, String> inputs = Map.of("input1", "Jonathan");
        assertEquals("Jo", ExpressionEvaluator.evaluate(expression, inputs));
    }

    @Test
    @DisplayName("Evaluates last:n correctly")
    void testLastChars() {
        String expression = "{input2|last:3}";
        Map<String, String> inputs = Map.of("input2", "Anderson");
        assertEquals("son", ExpressionEvaluator.evaluate(expression, inputs));
    }

    @Test
    @DisplayName("Evaluates all correctly")
    void testAllChars() {
        String expression = "{input3|all}";
        Map<String, String> inputs = Map.of("input3", "Hello");
        assertEquals("Hello", ExpressionEvaluator.evaluate(expression, inputs));
    }

    @Test
    @DisplayName("Throws exception for unknown function")
    void testThrowsOnUnknownFunction() {
        String expression = "{input1|someUnknownFunc:5}";
        Map<String, String> inputs = Map.of("input1", "Test");

        Exception exception = assertThrows(ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs));
        assertTrue(exception.getMessage().toLowerCase().contains("unknown"));
    }

    @Test
    @DisplayName("Throws exception for missing input")
    void testMissingInput() {
        String expression = "{input999|first:2}";
        Map<String, String> inputs = Map.of();

        ExpressionEvaluationException exception = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs)
        );

        assertEquals("Missing input for key: input999", exception.getMessage());
    }

    @Test
    @DisplayName("Supports quoted strings and glue")
    void testQuotedLiterals() {
        String expression = "\"prefix_\"~{input1|all}~\"_suffix\"";
        Map<String, String> inputs = Map.of("input1", "core");
        assertEquals("prefix_core_suffix", ExpressionEvaluator.evaluate(expression, inputs));
    }

    @Test
    @DisplayName("Supports chaining: first + lower")
    void testChainedFunctionCalls() {
        String expression = "{input1|first:2|lower}";
        Map<String, String> inputs = Map.of("input1", "Han");
        assertEquals("ha", ExpressionEvaluator.evaluate(expression, inputs));
    }

    @Test
    @DisplayName("Evaluates full email generation expression")
    void testComplexEmailExpression() {
        String expression = "{input1|first:1|lower}~'.'~{input2|last:3|lower}~'@'~{input3|all|lower}~'.com'";
        Map<String, String> inputs = Map.of(
            "input1", "Han",
            "input2", "Solo",
            "input3", "Galaxy"
        );
        assertEquals("h.olo@galaxy.com", ExpressionEvaluator.evaluate(expression, inputs));
    }

    @Test
    @DisplayName("Throws exception for malformed function call")
    void testMalformedFunctionCall() {
        String expression = "{input1|first:2";  // Missing closing '}'
        Map<String, String> inputs = Map.of("input1", "Test");

        assertThrows(ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs));
    }

    @Test
    @DisplayName("Throws exception for non-numeric argument")
    void testNonNumericArgument() {
        String expression = "{input1|first:x}";
        Map<String, String> inputs = Map.of("input1", "Hello");

        Exception exception = assertThrows(ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs));
        assertTrue(
            exception.getMessage().contains("Invalid argument") || exception.getMessage().contains("Malformed"));
    }

    @Test
    @DisplayName("Throws exception when expression is null")
    void testNullExpressionThrows() {
        ExpressionEvaluationException exception = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(null, Map.of("input1", "John"))
        );
        assertEquals("Missing or empty expression", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception when expression is blank")
    void testBlankExpressionThrows() {
        ExpressionEvaluationException exception = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate("   ", Map.of("input1", "John"))
        );
        assertEquals("Missing or empty expression", exception.getMessage());
    }

    @Test
    @DisplayName("Evaluates multiple expressions correctly")
    void testEvaluateAll_Success() {
        List<String> expressions = List.of(
            "{input1|first:1|lower}~'.'~{input2|all|lower}",
            "{input2|upper}~'_'~{input1|last:2|upper}"
        );
        Map<String, String> inputs = Map.of(
            "input1", "Han",
            "input2", "Solo"
        );

        List<String> results = ExpressionEvaluator.evaluateAll(expressions, inputs);

        assertEquals(2, results.size());
        assertEquals("h.solo", results.get(0));
        assertEquals("SOLO_AN", results.get(1));
    }

    @Test
    @DisplayName("Throws exception when expressions list is null")
    void testEvaluateAll_NullExpressions() {
        Map<String, String> inputs = Map.of("input1", "Han");

        ExpressionEvaluationException ex = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluateAll(null, inputs)
        );
        assertEquals("At least one expression is required.", ex.getMessage());
    }

    @Test
    @DisplayName("Throws exception when expressions list is empty")
    void testEvaluateAll_EmptyExpressions() {
        Map<String, String> inputs = Map.of("input1", "Han");

        ExpressionEvaluationException ex = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluateAll(List.of(), inputs)
        );
        assertEquals("At least one expression is required.", ex.getMessage());
    }

    @Test
    @DisplayName("Throws exception for unknown no-arg function")
    void testUnknownNoArgFunction() {
        String expression = "{input1|foobar}";
        Map<String, String> inputs = Map.of("input1", "Hello");

        Exception exception = assertThrows(ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs));

        assertTrue(exception.getMessage().contains("Unknown no-arg function"));
    }

    @Test
    @DisplayName("Throws exception for too many colons in function call")
    void testTooManyColonsInFunction() {
        String expression = "{input1|first:2:extra}";
        Map<String, String> inputs = Map.of("input1", "Hello");

        ExpressionEvaluationException ex = assertThrows(ExpressionEvaluationException.class,
            () -> ExpressionEvaluator.evaluate(expression, inputs));

        assertTrue(ex.getMessage().contains("Malformed function"));
    }

    @Test
    @DisplayName("Handles trailing empty function call gracefully")
    void testTrailingPipeIgnored() {
        String expression = "{input1|first:2|}";
        Map<String, String> inputs = Map.of("input1", "World");

        assertEquals("Wo", ExpressionEvaluator.evaluate(expression, inputs));
    }
}
