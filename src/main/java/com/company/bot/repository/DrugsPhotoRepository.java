package com.company.bot.repository;

import com.company.bot.entity.DrugsPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugsPhotoRepository extends JpaRepository<DrugsPhotoEntity, Long> {
    boolean deleteAllByUserId(long id);

    void removeAllByUserId(long id);

    boolean existsByUserId(long id);
}