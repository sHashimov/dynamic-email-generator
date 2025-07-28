package com.emailgen.util;

import com.emailgen.exception.ExpressionEvaluationException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

/**
 * The {@code ExpressionEvaluator} class provides a lightweight expression parser
 * to generate dynamic email strings based on user-defined inputs and transformation functions.
 *
 * <p>Supported syntax: {@code {input1|first:2|lower}} or literals like "@email.com".
 * Expressions are combined using the {@code ~} operator.
 *
 * <p>Supported functions:
 * <ul>
 *   <li>{@code first:n} - takes first {@code n} characters</li>
 *   <li>{@code last:n} - takes last {@code n} characters</li>
 *   <li>{@code all} - returns the entire value</li>
 *   <li>{@code lower} - converts to lowercase</li>
 *   <li>{@code upper} - converts to uppercase</li>
 * </ul>
 *
 * <p>Example: {@code {input1|first:1|lower} ~ "." ~ {input2|all|lower} ~ "@email.com"}
 */
@UtilityClass
public class ExpressionEvaluator {

    private static final Map<String, UnaryOperator<String>> NO_ARG_FUNCTIONS = Map.of(
        "lower", String::toLowerCase,
        "upper", String::toUpperCase,
        "all", s -> s
    );

    private static final Map<String, BiFunction<String, Integer, String>> ONE_ARG_FUNCTIONS = Map.of(
        "first", (s, n) -> s.length() >= n ? s.substring(0, n) : s,
        "last", (s, n) -> s.length() >= n ? s.substring(s.length() - n) : s
    );

    private static final Pattern INPUT_PATTERN = Pattern.compile("\\{(input\\d+)((\\|[a-z]+(:\\d+)?)+)?}");

    public static List<String> evaluateAll(List<String> expressions, Map<String, String> inputs) {
        if (expressions == null || expressions.isEmpty()) {
            throw new ExpressionEvaluationException("At least one expression is required.");
        }
        return expressions.stream()
            .map(expr -> evaluate(expr, inputs))
            .toList();
    }

    public static String evaluate(String expression, Map<String, String> inputs) {
        if (expression == null || expression.isBlank()) {
            throw new ExpressionEvaluationException("Missing or empty expression");
        }

        StringBuilder result = new StringBuilder();
        String[] parts = expression.split("~");

        for (String part : parts) {
            result.append(processPart(part.trim(), inputs));
        }

        return result.toString();
    }

    private static String processPart(String part, Map<String, String> inputs) {
        if (isQuotedLiteral(part)) {
            return part.substring(1, part.length() - 1);
        }

        Matcher matcher = INPUT_PATTERN.matcher(part);
        if (matcher.matches()) {
            String inputKey = matcher.group(1);
            String rawFunctionChain = matcher.group(2);

            if (!inputs.containsKey(inputKey)) {
                throw new ExpressionEvaluationException("Missing input for key: " + inputKey);
            }

            String inputValue = inputs.get(inputKey);
            return applyFunctions(inputValue, parseFunctionChain(rawFunctionChain));
        }

        throw new ExpressionEvaluationException("Malformed expression part: " + part);
    }

    private static boolean isQuotedLiteral(String part) {
        return (part.startsWith("\"") && part.endsWith("\"")) ||
            (part.startsWith("'") && part.endsWith("'"));
    }

    private static List<String> parseFunctionChain(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split("\\|")).filter(s -> !s.isBlank()).toList();
    }

    private static String applyFunctions(String input, List<String> functionCalls) {
        String result = input;
        for (String call : functionCalls) {
            result = applyFunction(result, call);
        }
        return result;
    }

    private static String applyFunction(String input, String funcCall) {
        String[] parts = funcCall.split(":");
        String functionName = parts[0];

        if (parts.length == 1) {
            UnaryOperator<String> func = NO_ARG_FUNCTIONS.get(functionName);
            if (func == null) {
                throw new ExpressionEvaluationException("Unknown no-arg function: " + functionName);
            }
            return func.apply(input);
        } else if (parts.length == 2) {
            try {
                int arg = Integer.parseInt(parts[1]);
                BiFunction<String, Integer, String> func = ONE_ARG_FUNCTIONS.get(functionName);
                if (func == null) {
                    throw new ExpressionEvaluationException("Unknown one-arg function: " + functionName);
                }
                return func.apply(input, arg);
            } catch (NumberFormatException e) {
                throw new ExpressionEvaluationException("Invalid argument for function " + functionName + ": " + parts[1]);
            }
        } else {
            throw new ExpressionEvaluationException("Malformed function: " + funcCall);
        }
    }
}