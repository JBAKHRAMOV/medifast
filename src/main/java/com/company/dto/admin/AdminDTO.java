package com.company.dto.admin;

import com.company.enums.admin.AdminStatus;
import com.company.enums.admin.BroadcastMSGStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDTO {
    private BroadcastMSGStatus broadcastMSGStatus;
    private AdminStatus status;

    private static AdminDTO instance = null;

    private AdminDTO() {
    }

    public static AdminDTO getInstance() {
        if (instance == null) {
            instance = new AdminDTO();
        }
        return instance;
    }

    public void clear() {
        status = null;
    }
}
