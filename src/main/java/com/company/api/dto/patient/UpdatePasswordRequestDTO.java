package com.company.api.dto.patient;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class UpdatePasswordRequestDTO {
    @NotBlank(message = "username required")
    private String username;
    @NotBlank(message = "old password required")
    private String oldPassword;
    @NotBlank(message = "old password required")
    @Size(min = 8, max = 255)
    private String newPassword;
}
