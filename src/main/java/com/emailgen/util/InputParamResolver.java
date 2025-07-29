package com.emailgen.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class InputParamResolver {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Resolves input parameters from either individual inputN params or from a fallback JSON string.
     *
     * @param queryParams   all request parameters
     * @param allParamsJson optional JSON fallback
     * @return a validated map of inputs
     * @throws IllegalArgumentException if input is missing or invalid
     */
    public Map<String, String> resolveInputs(Map<String, String> queryParams, String allParamsJson) {
        Map<String, String> inputs = queryParams.entrySet().stream()
            .filter(e -> e.getKey().matches("input\\d+"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (inputs.isEmpty() && allParamsJson != null && !allParamsJson.isBlank()) {
            try {
                inputs = mapper.readValue(allParamsJson, new TypeReference<>() {
                });
            } catch (Exception ex) {
                throw new IllegalArgumentException("Invalid allParams JSON format.");
            }
        }

        if (inputs.isEmpty()) {
            throw new IllegalArgumentException("At least one input parameter (e.g., input1) is required.");
        }

        return inputs;
    }
}
