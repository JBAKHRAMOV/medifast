package com.company.dto;

import com.company.enums.Gender;
import com.company.enums.LanguageCode;
import com.company.enums.UserQuestionnaireStatus;
import com.company.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BotUsersDTO implements Serializable {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String name;
    private String surname;
    private String phone;
    private LocalDate birthDate;
    private LanguageCode languageCode;
    private UserStatus status = UserStatus.NOT_ACTIVE;
    private UserQuestionnaireStatus questionnaireStatus = UserQuestionnaireStatus.DEFAULT;
    private Gender gender;
    private String weight;
    private String height;
    private Double currentTemperature;
    private Integer startLenght=0;
    private Integer finishLenght=2;
}
