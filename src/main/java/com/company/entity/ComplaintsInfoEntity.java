package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "complaints_info_entity")
public class ComplaintsInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private Long userId;
    @Column
    private String causeOfComplaint;
    @Column
    private String complaintStartedTime;
    @Column
    private String drugsList;
    @Column
    private String cigarette;
    @Column
    private String diseasesList;
    @Column
    private String inspectionPapers;

}