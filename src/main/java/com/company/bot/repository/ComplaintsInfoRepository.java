package com.company.bot.repository;

import com.company.bot.entity.ComplaintsInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintsInfoRepository extends JpaRepository<ComplaintsInfoEntity, Integer> {
    boolean deleteByUserId(long id);

    void removeAllByUserId(Long id);

    boolean existsByUserId(long id);
}