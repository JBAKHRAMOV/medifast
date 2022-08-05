package com.company.bot.repository;

import com.company.bot.entity.InspectionPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionPhotoRepository extends JpaRepository<InspectionPhotoEntity, Long> {
    boolean deleteAllByUserId(long id);

    void removeAllByUserId(long id);

    boolean existsByUserId(long id);
}