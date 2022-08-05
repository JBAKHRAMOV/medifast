package com.company.bot.repository;

import com.company.bot.entity.ComplaintsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintsRepository extends JpaRepository<ComplaintsEntity, Long> {
    List<ComplaintsEntity> findAllByUserId(Long id);

    boolean deleteAllByUserId(long id);
     void removeAllByUserId(long id);

    boolean existsByUserId(long id);

}