package com.company.api.entity;

import com.company.api.enums.PatientStatus;
import com.company.bot.entity.BaseEntity;
import com.company.bot.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patient")
@Getter
@Setter
public class PatientEntity extends BaseEntity {
    @Column
    private String name;
    @Column
    private String surname;
    @Column
    private String phone;
    @Column
    private LocalDate birthDate;
    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column
    private String weight;
    @Column
    private String height;
    @Column
    private String bloodPressure;
    @Column
    private String heartBeat;
    @Column
    private String diabetes;
    @Column
    private String temperature;
    @Column
    private String complaints;
    @Column
    private String causeOfComplaint;
    @Column
    private String complaintStartedTime;
    @Column
    private String drugsList;
    @Column
    private String cigarette;
    @Column
    private String diseasesList;
    @Column
    @Enumerated(EnumType.STRING)
    private PatientStatus status;
    @Column
    private String diagnosis;

}