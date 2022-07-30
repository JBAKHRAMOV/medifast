package com.company.api.dto;

import lombok.Data;

@Data
public class JwtDTO {
    private String name;
    private Long id;

    public JwtDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
