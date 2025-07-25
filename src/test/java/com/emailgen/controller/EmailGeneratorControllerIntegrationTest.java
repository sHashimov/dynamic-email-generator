package com.emailgen.controller;

import com.emailgen.DynamicEmailGeneratorApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DynamicEmailGeneratorApplication.class)
@AutoConfigureMockMvc
class EmailGeneratorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Returns generated email with valid inputs and expression")
    void testGenerateEmailSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/generate-email")
                .param("input1", "Jean")
                .param("input2", "Solo")
                .param("input3", "galaxy")
                .param("expression",
                    "input1.firstChars(1).lower()~'.'~input2.allChars().lower()~'@'~input3.allChars().lower()~\".com\""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].id").value("j.solo@galaxy.com"))
            .andExpect(jsonPath("$.data[0].value").value("j.solo@galaxy.com"));
    }

    @Test
    @DisplayName("Returns 400 when expression is missing")
    void testMissingExpressionParam() throws Exception {
        mockMvc.perform(get("/api/v1/generate-email")
                .param("input1", "Jean")
                .param("input2", "Solo"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Missing or empty 'expression' parameter"));
    }

    @Test
    @DisplayName("Returns 400 when expression is malformed")
    void testMalformedExpression() throws Exception {
        mockMvc.perform(get("/api/v1/generate-email")
                .param("input1", "Jean")
                .param("expression", "input1.firstChars(2"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Malformed function call")));
    }

    @Test
    @DisplayName("Returns 400 when function is unknown")
    void testUnknownFunctionInExpression() throws Exception {
        mockMvc.perform(get("/api/v1/generate-email")
                .param("input1", "Jean")
                .param("expression", "input1.unknownFunc(5)"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Unknown")));
    }

    @Test
    @DisplayName("Handles missing inputN gracefully")
    void testMissingInputKeyGraceful() throws Exception {
        mockMvc.perform(get("/api/v1/generate-email")
                .param("expression", "input999.firstChars(2)"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].value").value(""));
    }
}
