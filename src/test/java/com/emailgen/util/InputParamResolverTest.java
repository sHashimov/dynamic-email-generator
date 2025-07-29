package com.emailgen.util;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InputParamResolverTest {

    @Test
    void resolvesInputsFromQueryParams() {
        Map<String, String> result = InputParamResolver.resolveInputs(
            Map.of("input1", "John", "other", "value"),
            null
        );
        assertEquals(Map.of("input1", "John"), result);
    }

    @Test
    void resolvesInputsFromAllParamsJson() {
        String json = "{\"input1\":\"Alice\",\"input2\":\"Smith\"}";
        Map<String, String> result = InputParamResolver.resolveInputs(Collections.emptyMap(), json);
        assertEquals(Map.of("input1", "Alice", "input2", "Smith"), result);
    }

    @Test
    void throwsWhenInputsMissing() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            InputParamResolver.resolveInputs(Collections.emptyMap(), null)
        );
        assertTrue(ex.getMessage().contains("At least one input parameter"));
    }

    @Test
    void throwsOnInvalidJson() {
        String invalidJson = "{not:valid";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            InputParamResolver.resolveInputs(Collections.emptyMap(), invalidJson)
        );
        assertTrue(ex.getMessage().contains("Invalid allParams JSON format."));
    }
}
