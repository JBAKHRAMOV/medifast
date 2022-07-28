package com.company.bot.repository;

import com.company.bot.entity.ComplaintsInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintsInfoRepository extends JpaRepository<ComplaintsInfoEntity, Integer> {
}