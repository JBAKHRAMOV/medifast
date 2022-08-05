package com.company.api.dto.patient;

import com.company.api.dto.ImageDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PatientFullResponseDTO {
    PatientDTO patient;
    List<ImageDTO> imageList;
}
