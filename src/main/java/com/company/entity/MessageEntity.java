package com.company.entity;

import com.company.enums.LanguageCode;
import com.company.enums.MessageType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "message")
public class MessageEntity extends BaseEntity {
    @Column(columnDefinition = "text")
    private String text;
    @Column
    @Enumerated(EnumType.STRING)
    private LanguageCode languageCode;

    @Column
    @Enumerated(EnumType.STRING)
    private MessageType type;
}