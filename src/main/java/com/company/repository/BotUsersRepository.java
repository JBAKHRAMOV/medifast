package com.company.repository;

import com.company.entity.BotUsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotUsersRepository extends JpaRepository<BotUsersEntity, Long> {
    Optional<BotUsersEntity> findByTelegramId(Long telegramId);

}