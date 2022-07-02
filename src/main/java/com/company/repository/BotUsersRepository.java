package com.company.repository;

import com.company.entity.BotUsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface BotUsersRepository extends JpaRepository<BotUsersEntity, Long> {
    Optional<BotUsersEntity> findByTelegramId(Long telegramId);


    @Query(value = """
            select count(id)
            from users
            where created_date::date = current_date;
            """, nativeQuery = true)
    Integer joinedToday();

    @Query(value = """
            select count(id)
            from users
            where created_date::date > current_date - interval '3' day;
            """, nativeQuery = true)
    Integer joinedLastThreeDays();

    @Query(value = """
            select count(id)
            from users;
            """, nativeQuery = true)
    Integer countAllUsers();

    @Query(value = """
            select count(id)
            from users
            where created_date::date > current_date - interval '1' month;
            """, nativeQuery = true)
    Integer joinedLastOneMonth();
}