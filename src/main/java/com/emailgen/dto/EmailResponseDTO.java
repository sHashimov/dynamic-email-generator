package com.emailgen.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailResponseDTO {
    private List<EmailResultItem> data;
}
