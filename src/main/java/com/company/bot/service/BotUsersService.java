package com.company.bot.service;

import com.company.bot.entity.BotUsersEntity;
import com.company.bot.repository.BotUsersRepository;
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
        var user = botUsersRepository.findByTelegramId(entity.getTelegramId());
        if (user.isEmpty())
            botUsersRepository.save(entity);
        else {
            botUsersRepository.delete(user.get());
            botUsersRepository.save(entity);
        }
    }

}
