package com.company.api.dto.authorization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDTO {
    @NotBlank(message = "username required!")
    private String username;
    @NotBlank(message = "password required!")
    private String password;
}
