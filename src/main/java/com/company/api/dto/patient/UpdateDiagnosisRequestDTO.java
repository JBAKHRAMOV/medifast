package com.company.api.dto.patient;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Builder
@Data
public class UpdateDiagnosisRequestDTO {
    @Positive(message = "patientId required")
    private Long patientId;
    @NotNull(message = "diagnosis required")
    private String diagnosis;
}
