package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.entity.QuestionnaireEntity;
import com.company.enums.UserQuestionnaireStatus;
import com.company.service.BotUsersService;
import com.company.service.MessageService;
import com.company.service.QuestionnaireService;
import com.company.util.button.ButtonUtil;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.company.enums.UserStatus.FILL_FORM;


@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final QuestionnaireService questionnaireService;

    public void messageController(Message message) {
        String text = "";

        if (message.getText() != null) {
            text = message.getText();
        }

        if (message.hasContact())
            text = message.getContact().getPhoneNumber();

        if (text.equals("/start")) {

            TelegramBotConfig.USER_LIST.put(message.getChatId(), new BotUsersDTO());

            var sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setText("Iltimos, tilni tanlang. / Пожалуйста, выберите язык.");
            sendMessage.setReplyMarkup(InlineButtonUtil.languageButtons());

            telegramBotConfig.sendMsg(sendMessage);
        }
        var botUser = TelegramBotConfig.USER_LIST.get(message.getChatId());

        if (botUser.getStatus().equals(FILL_FORM)
                || text.equals(ButtonUtil.FILL_FORM_BTN_UZ)
                || text.equals(ButtonUtil.FILL_FORM_BTN_RU)) {

            botUser.setStatus(FILL_FORM);

            if (botUser.getQuestionnaireStatus().equals(UserQuestionnaireStatus.DEFAULT)) {
                botUser.setQuestionnaireStatus(UserQuestionnaireStatus.NAME);
                TelegramBotConfig.USER_LIST.put(message.getChatId(), botUser);
            }

            questionnaireService.create(message);
        }

        if (text.equals("\uD83E\uDD16 Bot haqida ma'lumot") || text.equals("\uD83E\uDD16 Информация о боте")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setText("Bot haqida ma'lumot qismi hali ishlab chiqish jarayonida,\nIltimos yangilik bo'lishini kuting! /start");

            telegramBotConfig.sendMsg(sendMessage);
        }
    }
}
