package com.company.repository;

import com.company.entity.ComplaintsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintsRepository extends JpaRepository<ComplaintsEntity, Long> {
}