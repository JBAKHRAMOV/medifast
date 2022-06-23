package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.entity.BotUsersEntity;
import com.company.entity.QuestionnaireEntity;
import com.company.enums.UserQuestionnaireStatus;
import com.company.enums.UserStatus;
import com.company.service.BotUsersService;
import com.company.service.MessageService;
import com.company.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.company.enums.UserStatus.*;


@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {
    @Lazy
    private final MessageService messageService;
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final BotUsersService botUsersService;
    private final QuestionnaireService questionnaireService;

    public void messageController(Message message) {

        if (message.getText() == null && !message.hasContact()) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));

            sendMessage.setText("""
                    ❗️Iltimos, menga tekst jo'nating\s
                    ➖➖➖➖➖➖➖➖➖➖➖
                    ❗️ Пожалуйста, пришлите мне текст
                    """);
            telegramBotConfig.sendMsg(sendMessage);
            return;
        }

        var text = message.getText();

        if (message.hasContact())
            text = message.getContact().getPhoneNumber();

        if (text.equals("/start")) {
            messageService.handleStartMessage(message, message.getFrom());
        }

        var botUser = botUsersService.getByTelegramId(message.getChatId());
        if (botUser == null) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));

            sendMessage.setText("""
                    ❗️/start buyrug'ini bosib, qaytadan urinib ko'ring.\s
                    ➖➖➖➖➖➖➖➖➖➖➖
                    ❗️Попробуйте еще раз, нажав команду  /start.
                    """);
            telegramBotConfig.sendMsg(sendMessage);
            return;
        }

        if (botUser.getStatus().equals(FILL_FORM)
                || text.equals("✍️ Anketa to'ldirish")
                || text.equals("✍️ Заполните анкету")) {

            botUser.setStatus(FILL_FORM);

            var entity = questionnaireService.getByTelegramId(message.getFrom().getId());
            if (entity == null) {
                var questionnaire = new QuestionnaireEntity();
                questionnaire.setStatus(UserQuestionnaireStatus.NAME);
                questionnaire.setTelegramId(message.getFrom().getId());

                questionnaireService.save(questionnaire);
                questionnaireService.create(message, questionnaire);
            } else
                questionnaireService.create(message, entity);

            botUsersService.saveUser(botUser);
        }

        if (text.equals("\uD83E\uDD16 Bot haqida ma'lumot") || text.equals("\uD83E\uDD16 Информация о боте")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setText("Bot haqida ma'lumot qismi hali ishlab chiqish jarayonida,\nIltimos yangilik bo'lishini kuting! /start");

            telegramBotConfig.sendMsg(sendMessage);
        }
    }
}
