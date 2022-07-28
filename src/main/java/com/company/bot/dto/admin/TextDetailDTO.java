package com.company.bot.dto.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextDetailDTO {
    private String text;
    private boolean hasText;
    private static TextDetailDTO instance = null;

    private TextDetailDTO() {
    }

    public static TextDetailDTO getInstance() {
        if (instance == null) {
            instance = new TextDetailDTO();
        }
        return instance;
    }
    public void clear(){
        text = null;
        hasText = false;
    }
}
