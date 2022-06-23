package com.company.dto;

import com.company.enums.LanguageCode;
import com.company.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDTO implements Serializable {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String text;
    private LanguageCode languageCode;
    private MessageType type;
}
