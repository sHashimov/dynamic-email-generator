package com.emailgen.dto.email;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailResponseDTO {
    private List<EmailResultItem> data;
}
