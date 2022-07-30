package com.company.api.dto.authorization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDTO {
    private String username;
    private String password;
}
