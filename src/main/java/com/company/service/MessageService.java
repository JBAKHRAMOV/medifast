package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.dto.MessageDTO;
import com.company.entity.BotUsersEntity;
import com.company.entity.MessageEntity;
import com.company.enums.LanguageCode;
import com.company.enums.MessageType;
import com.company.repository.BotUsersRepository;
import com.company.repository.MessageRepository;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;


@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    @Value("${user.admin}")
    private Long adminId;
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final BotUsersRepository botUsersRepository;
    private final MessageRepository messageRepository;

    public void handleStartMessage(Message message, User user) {

        var optional = botUsersRepository.findByTelegramId(message.getFrom().getId());

        if (optional.isEmpty()) {
            botUsersRepository.save(new BotUsersEntity(user.getId()));
        }

        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Iltimos, tilni tanlang. / Пожалуйста, выберите язык.");
        sendMessage.setReplyMarkup(InlineButtonUtil.languageButtons());

        telegramBotConfig.sendMsg(sendMessage);
    }

    public String create(MessageDTO dto, Long userId) {
        if (!userId.equals(adminId))
            return "Kechirasiz, ushbu buyruq faqat admin uchun!";

        var entity = new MessageEntity();

        entity.setText(dto.getText());
        entity.setType(dto.getType());
        entity.setLanguageCode(dto.getLanguageCode());

        messageRepository.save(entity);

        return "📌 Muvaffaqiyatli yaratildi!";
    }

    public MessageEntity getByTypeAndLangCode(MessageType type, LanguageCode languageCode) {
        return messageRepository.getByTypeAndLanguageCode(type, languageCode).orElse(null);
    }
}
