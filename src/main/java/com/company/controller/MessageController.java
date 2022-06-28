package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.service.MessageService;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.enums.ButtonName.FILL_FORM_BTN_RU;
import static com.company.enums.ButtonName.FILL_FORM_BTN_UZ;
import static com.company.enums.UserStatus.FILL_FORM;
import static com.company.service.MessageService.defaultS;


@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final MessageService messageService;

    public void messageController(Message message) {
        var text = "";
        var user = USER_LIST.get(message.getChatId());

        if (message.getText() != null) text = message.getText();

        if (message.hasContact()) text = message.getContact().getPhoneNumber();

        if (text.equals("/start")) start(message);

        else if (user.getStatus().equals(FILL_FORM)
                || text.equals(FILL_FORM_BTN_UZ)
                || text.equals(FILL_FORM_BTN_RU)) {
            user.setStatus(FILL_FORM);
            fillFrom(message, user);
        }
    }

    private void start(Message message) {
        USER_LIST.put(message.getChatId(), new BotUsersDTO());

        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText("Iltimos, tilni tanlang. / Пожалуйста, выберите язык.");
        sendMessage.setReplyMarkup(InlineButtonUtil.languageButtons());

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void fillFrom(Message message, BotUsersDTO user) {
        var qStatus = user.getQuestionnaireStatus();
        var lang = user.getLanguageCode();

        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());


        switch (lang) {
            case UZ -> {
                switch (qStatus) {
                    case DEFAULT -> defaultS(message, user);
                    case NAME -> messageService.name(message, user, sendMessage);
                    case SURNAME -> messageService.surname(message, user, sendMessage);
                    case BIRTH_DATE -> messageService.birthDate(message, user, sendMessage);
                    case GENDER -> messageService.gender(message, user, sendMessage);
                    case HEIGHT -> messageService.height(message, user, sendMessage);
                    case WEIGHT -> messageService.weight(message, user, sendMessage);
                    case PHONE -> messageService.phone(message, user, sendMessage);
                }
            }
            case RU -> {
                switch (qStatus) {
                    case DEFAULT -> messageService.rus(sendMessage);
                    case NAME -> messageService.rus(sendMessage);
                    case SURNAME -> messageService.rus(sendMessage);
                    case BIRTH_DATE -> messageService.rus(sendMessage);
                    case GENDER -> messageService.rus(sendMessage);
                    case HEIGHT -> messageService.rus(sendMessage);
                    case WEIGHT -> messageService.rus(sendMessage);
                    case PHONE -> messageService.rus(sendMessage);
                }
            }

        }
    }
}
