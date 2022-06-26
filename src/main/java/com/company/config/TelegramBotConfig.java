package com.company.config;

import com.company.controller.CallBackQueryController;
import com.company.controller.MessageController;
import com.company.dto.BotUsersDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.WeakHashMap;

@Component
@Slf4j
public class TelegramBotConfig extends TelegramLongPollingBot {

    public static final WeakHashMap<Long, BotUsersDTO> USER_LIST = new WeakHashMap<>();
    //test
    @Autowired
    @Lazy
    private MessageController messageController;

    @Autowired
    @Lazy
    private CallBackQueryController callBackQueryController;

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage())
            messageController.messageController(update.getMessage());
        if (update.hasCallbackQuery())
            callBackQueryController
                    .callBackQueryController(update.getCallbackQuery());
    }

    public void sendMsg(Object obj) {
        try {
            if (obj instanceof SendMessage)
                execute((SendMessage) obj);
            else if (obj instanceof SendPhoto)
                execute((SendPhoto) obj);
            else if (obj instanceof SendVideo)
                execute((SendVideo) obj);
            else if (obj instanceof SendLocation)
                execute((SendLocation) obj);
            else if (obj instanceof SendContact)
                execute((SendContact) obj);
            else if (obj instanceof EditMessageText)
                execute((EditMessageText) obj);
            else if (obj instanceof SendDocument)
                execute((SendDocument) obj);
            else if (obj instanceof DeleteMessage)
                execute((DeleteMessage) obj);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
