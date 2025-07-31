package com.emailgen.dto.email;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class EmailGenerationRequestDTO {
    @NotNull(message = "Inputs map must be provided")
    @NotEmpty(message = "Inputs must not be empty")
    private Map<String, String> inputs;
    @NotNull(message = "Expressions list must be provided")
    @NotEmpty(message = "At least one expression is required")
    private List<String> expressions;
}
