package com.company.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfDTO {
    private BotUsersDTO user;
    private ComplaintsInfoDTO complaintsInfoDTO;
    private List<ComplaintsDTO> complaintsList;
    private List<UserPhotoDTO> drugsList;
    private List<UserPhotoDTO> inspectionList;
}
