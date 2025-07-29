package com.emailgen.dto.email;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class EmailGenerationRequestDTO {
    private Map<String, String> inputs;
    private List<String> expressions;
}
