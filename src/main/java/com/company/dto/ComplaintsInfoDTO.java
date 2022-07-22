package com.company.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintsInfoDTO {
    private String causeOfComplaint;
    private String complaintStartedTime;
    private String drugsList = " ";
    private String cigarette;
    private String diseasesList;
    private String inspectionPapers;
}
