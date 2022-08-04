package com.company.api.repo;

import com.company.api.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    @Transactional
    @Modifying
    @Query("update PatientEntity set lastModifiedDate=?1 where id=?2")
    void updateLastModifiedDate(LocalDateTime lastModifiedDate, Long id);
}