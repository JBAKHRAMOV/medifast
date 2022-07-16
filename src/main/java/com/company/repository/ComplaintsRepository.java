package com.company.repository;

import com.company.entity.ComplaintsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintsRepository extends JpaRepository<ComplaintsEntity, Long> {
    List<ComplaintsEntity> findAllByUserId(Long id);

}