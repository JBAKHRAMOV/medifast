package com.company.bot.dto.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhotoDetailDTO {
    private String fileId;

    private String caption;
    private boolean hasPhoto;

    private static PhotoDetailDTO instance = null;

    private PhotoDetailDTO() {
    }

    public static PhotoDetailDTO getInstance() {
        if (instance == null) {
            instance = new PhotoDetailDTO();
        }
        return instance;
    }

    public void clear() {
        fileId = null;
        caption = null;
        hasPhoto = false;
    }
}
