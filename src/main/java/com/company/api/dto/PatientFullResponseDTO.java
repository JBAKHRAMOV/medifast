package com.company.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PatientFullResponseDTO {
    PatientDTO patient;
    List<ImageDTO> imageList;
}
