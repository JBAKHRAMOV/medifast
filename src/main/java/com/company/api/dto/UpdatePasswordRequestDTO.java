package com.company.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePasswordRequestDTO {
    private String username;
    private String oldPassword;
    private String newPassword;
}
