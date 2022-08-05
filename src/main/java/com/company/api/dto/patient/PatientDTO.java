package com.company.api.dto.patient;

import com.company.api.enums.PatientStatus;
import com.company.bot.enums.Gender;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientDTO implements Serializable {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String name;
    private String surname;
    private String phone;
    private LocalDate birthDate;
    private Gender gender;
    private String weight;
    private String height;
    private Double currentTemperature;
    private String bloodPressure;
    private String heartBeat;
    private String diabetes;
    private String temperature;
    private String complaints;
    private String causeOfComplaint;
    private String complaintStartedTime;
    private String drugsList;
    private String cigarette;
    private String diseasesList;
    private PatientStatus status;
}
