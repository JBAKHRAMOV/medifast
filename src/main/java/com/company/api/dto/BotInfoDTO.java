package com.company.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BotInfoDTO {
    private Integer totalUsers;
    private Integer joinedToday;
}
