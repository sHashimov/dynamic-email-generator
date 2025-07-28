package com.emailgen.controller;

import com.emailgen.dto.EmailGenerationRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmailGeneratorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Returns generated email with valid inputs and expressions")
    void testGenerateEmailSuccess() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of(
            "input1", "Jean",
            "input2", "Solo",
            "input3", "galaxy"
        ));
        request.setExpressions(List.of(
            "{input1|first:1|lower}~'.'~{input2|all|lower}~'@'~{input3|all|lower}~\".com\""
        ));

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].id").value("j.solo@galaxy.com"))
            .andExpect(jsonPath("$.data[0].value").value("j.solo@galaxy.com"));
    }

    @Test
    @DisplayName("Returns 400 when expressions list is missing or empty")
    void testMissingExpressionsList() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of("input1", "Jean"));
        request.setExpressions(null);

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BadRequest"))
            .andExpect(jsonPath("$.message", containsString("At least one expression")));
    }

    @Test
    @DisplayName("Returns 400 when expression is malformed")
    void testMalformedExpression() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of("input1", "Jean"));
        request.setExpressions(List.of("{input1|first:2"));

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("ExpressionError")))
            .andExpect(jsonPath("$.message", containsString("Malformed expression part")));
    }

    @Test
    @DisplayName("Returns 400 when function is unknown")
    void testUnknownFunctionInExpression() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of("input1", "Jean"));
        request.setExpressions(List.of("{input1|unknownFunc:5}"));

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("ExpressionError")))
            .andExpect(jsonPath("$.message", containsString("Malformed expression part")));
    }

    @Test
    @DisplayName("Returns 400 when referenced input key is missing")
    void testMissingInputKeyGraceful() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of());
        request.setExpressions(List.of("{input999|first:2}"));

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("ExpressionError")))
            .andExpect(jsonPath("$.message", is("Missing input for key: input999")));
    }

    @Test
    @DisplayName("Returns 400 when input key is invalid")
    void testInvalidInputKey() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of("badKey", "Han"));
        request.setExpressions(List.of("{input1|first:1}"));

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BadRequest"))
            .andExpect(jsonPath("$.message").value("Invalid input key: badKey"));
    }

    @Test
    @DisplayName("Returns 400 when an input key referenced in expression is missing")
    void testMissingInputKeyInExpression() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of("input2", "Solo"));
        request.setExpressions(List.of("{input1|first:1}~'.'~{input2|all}~'@galaxy.com'"));

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("ExpressionError")))
            .andExpect(jsonPath("$.message", is("Missing input for key: input1")));
    }
}
