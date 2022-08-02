package com.company.api.dto;

import com.company.api.enums.PatientStatus;
import com.company.bot.entity.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientDTO implements Serializable {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private BotUsersEntity botUsers;
    private ComplaintsEntity complaints;
    private ComplaintsInfoEntity complaintsInfo;
    private DrugsPhotoEntity drugsPhoto;
    private InspectionPhotoEntity inspectionPhoto;
    private PatientStatus status;
}
