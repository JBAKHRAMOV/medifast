package com.company.repository;

import com.company.entity.MessageEntity;
import com.company.enums.LanguageCode;
import com.company.enums.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    Optional<MessageEntity> getByTypeAndLanguageCode(MessageType type, LanguageCode languageCode);
}