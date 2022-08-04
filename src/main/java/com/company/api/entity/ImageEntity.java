package com.company.api.entity;

import com.company.api.enums.ImageType;
import com.company.bot.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "image")
public class ImageEntity extends BaseEntity {
    @Column
    private String link;
    @Column
    @Enumerated(EnumType.STRING)
    private ImageType type;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;
}