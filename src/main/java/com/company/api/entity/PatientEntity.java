package com.company.api.entity;

import com.company.api.enums.PatientStatus;
import com.company.bot.entity.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "patient")
@Getter
@Setter
public class PatientEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "bot_users_id")
    private BotUsersEntity botUsers;

    @ManyToOne
    @JoinColumn(name = "complaints_id")
    private ComplaintsEntity complaints;

    @ManyToOne
    @JoinColumn(name = "complaints_info_id")
    private ComplaintsInfoEntity complaintsInfo;

    @ManyToOne
    @JoinColumn(name = "drugs_photo_id")
    private DrugsPhotoEntity drugsPhoto;

    @ManyToOne
    @JoinColumn(name = "inspection_photo_id")
    private InspectionPhotoEntity inspectionPhoto;

    private PatientStatus status;

}