package com.emailgen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EmailResponseDTO {
    private List<EmailResultItem> data;
}
