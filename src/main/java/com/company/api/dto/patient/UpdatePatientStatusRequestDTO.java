package com.company.api.dto.patient;

import com.company.api.enums.PatientStatus;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class UpdatePatientStatusRequestDTO {
    @NotNull(message = "Status required")
    private PatientStatus status;
    @Positive(message = "Patient id required")
    private Long patientId;
}
