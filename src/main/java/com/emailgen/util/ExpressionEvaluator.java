package com.emailgen.util;

import com.emailgen.exception.ExpressionEvaluationException;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

/**
 * The {@code ExpressionEvaluator} class provides a basic custom expression engine to generate dynamic email
 * addresses based on user-defined expressions and input values.
 * <p>
 * It supports the following functions on input parameters:
 * <ul>
 *   <li>{@code firstChars(n)} - extracts the first {@code n} characters</li>
 *   <li>{@code lastChars(n)} - extracts the last {@code n} characters</li>
 *   <li>{@code allChars()} - returns the full value</li>
 *   <li>{@code lower()} - transforms all characters to lowercase</li>
 *   <li>{@code upper()} - transforms all characters to uppercase</li>
 * </ul>
 * Expressions are built by concatenating inputs, literals, and functions using the {@code ~} operator.
 * Example:
 * <pre>
 *     input1.firstChars(1) ~ "." ~ input2.allChars() ~ "@email.com"
 * </pre>
 */
@UtilityClass
public class ExpressionEvaluator {

    private static final Map<String, UnaryOperator<String>> NO_ARG_FUNCTIONS
        = Map.of(
        "lower", String::toLowerCase,
        "upper", String::toUpperCase,
        "allChars", s -> s
    );
    private static final Map<String, BiFunction<String, Integer, String>> ONE_ARG_FUNCTIONS = Map.of(
        "firstChars", (s, n) -> s.length() >= n ? s.substring(0, n) : s,
        "lastChars", (s, n) -> s.length() >= n ? s.substring(s.length() - n) : s
    );
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("(\\w+)\\((\\d*)\\)");
    private static final String INPUT_PREFIX = "input";

    /**
     * Evaluates the given expression string using provided dynamic input values.
     *
     * @param expression the custom expression string to evaluate
     * @param inputs     a map of dynamic input values (e.g., input1 → "John")
     * @return the evaluated string (e.g., "j.doe@email.com")
     */
    public static String evaluate(String expression, Map<String, String> inputs) {
        if (expression == null || expression.isBlank()) {
            throw new ExpressionEvaluationException("Missing or empty expression");
        }

        String[] parts = expression.split("~");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            result.append(processExpressionPart(part.trim(), inputs));
        }

        return result.toString();
    }

    private static String processExpressionPart(String part, Map<String, String> inputs) {
        if (isQuotedLiteral(part)) {
            return part.substring(1, part.length() - 1);
        }

        if (part.startsWith(INPUT_PREFIX)) {
            return processInputReference(part, inputs);
        }

        return part;
    }

    private static boolean isQuotedLiteral(String part) {
        return (part.startsWith("\"") && part.endsWith("\"")) ||
            (part.startsWith("'") && part.endsWith("'"));
    }

    private static String processInputReference(String part, Map<String, String> inputs) {
        int dotIndex = part.indexOf('.');
        String inputKey = dotIndex > 0 ? part.substring(0, dotIndex) : part;
        String functionChain = dotIndex > 0 ? part.substring(dotIndex + 1) : null;

        if (!inputs.containsKey(inputKey)) {
            throw new ExpressionEvaluationException("Missing input for key: " + inputKey);
        }

        String inputValue = inputs.get(inputKey);
        return applyFunction(inputValue, functionChain);
    }

    private static String applyFunction(String input, String fullFunctionChain) {
        if (fullFunctionChain == null || fullFunctionChain.isBlank()) {
            return input;
        }

        String result = input;
        String[] functionCalls = fullFunctionChain.split("\\.");

        for (String call : functionCalls) {
            result = applySingleFunction(result, call);
        }

        return result;
    }

    private static String applySingleFunction(String input, String funcCall) {
        if (funcCall == null || funcCall.isBlank()) {
            return input;
        }

        Matcher matcher = FUNCTION_PATTERN.matcher(funcCall);
        if (!matcher.matches()) {
            throw new ExpressionEvaluationException("Malformed function call: " + funcCall);
        }

        String functionName = matcher.group(1);
        String arg = matcher.group(2);

        if (arg == null || arg.isEmpty()) {
            UnaryOperator<String> func = NO_ARG_FUNCTIONS.get(functionName);
            if (func == null) {
                throw new ExpressionEvaluationException("Unknown no-arg function: " + functionName);
            }
            return func.apply(input);
        } else {
            int n;
            try {
                n = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                throw new ExpressionEvaluationException(
                    "Invalid argument for function " + functionName + ": " + arg);
            }

            BiFunction<String, Integer, String> func = ONE_ARG_FUNCTIONS.get(functionName);
            if (func == null) {
                throw new ExpressionEvaluationException("Unknown one-arg function: " + functionName);
            }
            return func.apply(input, n);
        }
    }
}
