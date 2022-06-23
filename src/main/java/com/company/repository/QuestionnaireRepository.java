package com.company.repository;


import com.company.entity.QuestionnaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionnaireRepository extends JpaRepository<QuestionnaireEntity, Long> {

    Optional<QuestionnaireEntity> findByTelegramId(Long telegramId);
}