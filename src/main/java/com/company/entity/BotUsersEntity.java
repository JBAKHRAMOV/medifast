package com.company.entity;

import com.company.enums.Gender;
import com.company.enums.LanguageCode;
import com.company.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class BotUsersEntity extends BaseEntity {

    public BotUsersEntity(Long telegramId) {
        this.telegramId = telegramId;
    }

    @Column
    private Long telegramId;

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
    private LanguageCode languageCode;

    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.NOT_ACTIVE;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String weight;

    @Column
    private String height;

    @Column
    private Double currentTemperature;
}