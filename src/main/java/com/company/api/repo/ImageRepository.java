package com.company.api.repo;

import com.company.api.entity.ImageEntity;
import com.company.api.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    List<ImageEntity> findAllByPatient(PatientEntity patient);
}