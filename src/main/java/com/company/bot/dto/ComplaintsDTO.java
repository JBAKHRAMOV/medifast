package com.company.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintsDTO {
    private Long id;
    private String nameUz;
    private String nameRu;
    private String key;
}
