package com.company.entity;

import com.company.enums.Gender;
import com.company.enums.UserQuestionnaireStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "questionnaire")
public class QuestionnaireEntity extends BaseEntity {
    @Column
    private String name, surname;

    @Column
    private Long telegramId;

    @Column
    private LocalDate birthDate;

    @Column
    private String phone;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String weight;

    @Column
    private String height;

    @Column
    private Double currentTemperature;

    @Column
    @Enumerated(EnumType.STRING)
    private UserQuestionnaireStatus status;

}