package com.company.repository;

import com.company.entity.ComplaintsInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintsInfoRepository extends JpaRepository<ComplaintsInfoEntity, Integer> {
}