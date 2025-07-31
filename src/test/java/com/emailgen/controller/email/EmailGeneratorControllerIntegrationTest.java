package com.emailgen.controller.email;

import com.emailgen.dto.email.EmailGenerationRequestDTO;
import com.emailgen.security.Roles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "testuser", roles = Roles.ADMIN)
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
    @DisplayName("Returns 422 when expressions list is missing or empty")
    void testMissingExpressionsList() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of("input1", "Jean"));
        request.setExpressions(null);

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").value("ValidationError"))
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
            .andExpect(jsonPath("$.message", containsString("Unknown one-arg function")));
    }

    @Test
    @DisplayName("Returns 422 when referenced input key is missing")
    void testMissingInputKeyGraceful() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of());
        request.setExpressions(List.of("{input999|first:2}"));

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").value("ValidationError"))
            .andExpect(jsonPath("$.message", is("inputs: Inputs must not be empty")));
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
            .andExpect(jsonPath("$.error").value("ExpressionError"))
            .andExpect(jsonPath("$.message").value("Missing input for key: input1"));
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

    @Test
    @DisplayName("GET /generate-email - Returns email from valid query parameters")
    void testGenerateEmailViaQueryParams_Success() throws Exception {
        mockMvc.perform(get("/api/v1/generate-email")
                .param("expression", "{input1|first:1|lower}~'.'~{input2|all|lower}~'@example.com'")
                .param("input1", "Jane")
                .param("input2", "Doe"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data[0].value").value("j.doe@example.com"));
    }

    @Test
    @DisplayName("GET /generate-email - Missing expression parameter returns 400")
    void testGenerateEmailViaQueryParams_MissingExpression() throws Exception {
        mockMvc.perform(get("/api/v1/generate-email")
                .param("input1", "Jane")
                .param("input2", "Doe"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("expression")));
    }

    @Test
    @DisplayName("GET /generate-email - Invalid input key returns 400")
    void testGenerateEmailViaQueryParams_InvalidInputKey() throws Exception {
        mockMvc.perform(get("/api/v1/generate-email")
                .param("expression", "{input1|all|lower}")
                .param("name", "John"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("input")));
    }

    @Test
    @WithMockUser(username = "intruder", roles = Roles.GUEST)
    @DisplayName("Access denied for user with unauthorized role")
    void testAccessDeniedForUnauthorizedRole() throws Exception {
        EmailGenerationRequestDTO request = new EmailGenerationRequestDTO();
        request.setInputs(Map.of("input1", "Jean"));
        request.setExpressions(List.of("{input1|first:1}"));

        mockMvc.perform(post("/api/v1/generate-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }


}
