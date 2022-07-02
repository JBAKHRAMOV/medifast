package com.company.service;

import com.company.dto.BotUsersDTO;
import com.company.entity.BotUsersEntity;
import com.company.repository.BotUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotUsersService {
    private final BotUsersRepository botUsersRepository;

    public BotUsersEntity getByTelegramId(Long telegramId) {
        return botUsersRepository.findByTelegramId(telegramId).orElse(null);
    }

    public void saveUser(BotUsersEntity entity) {
        botUsersRepository.save(entity);
    }

}
