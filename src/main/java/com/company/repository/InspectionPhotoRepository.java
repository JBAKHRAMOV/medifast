package com.company.repository;

import com.company.entity.InspectionPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionPhotoRepository extends JpaRepository<InspectionPhotoEntity, Long> {
}