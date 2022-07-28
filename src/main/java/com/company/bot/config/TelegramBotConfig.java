package com.company.bot.config;

import com.company.bot.controller.CallBackQueryController;
import com.company.bot.controller.MessageController;
import com.company.bot.dto.ComplaintsDTO;
import com.company.bot.dto.ComplaintsInfoDTO;
import com.company.bot.dto.UserPhotoDTO;
import com.company.bot.controller.ValidationController;
import com.company.bot.dto.BotUsersDTO;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j

public class TelegramBotConfig extends TelegramLongPollingBot {

    public static final Map<Long, BotUsersDTO> USER_LIST = new HashMap<>();// userlar
    public static final Map<Long, ComplaintsInfoDTO> USER_COMPLAINT_INFO = new HashMap<>();// shikotlar yozilgani
    public static final Map<Long, List<ComplaintsDTO>> USER_COMPLAINT = new HashMap();// Shikoyatlar button
    public static final Map<Long, List<UserPhotoDTO>> USER_PHOTOS_DRUGS = new HashMap<>();// dorilar rasmlari

    public static final Map<Long, List<UserPhotoDTO>> USER_PHOTOS_INSPECTION = new HashMap<>();// tekshiruv rasmlari
    //test
    @Lazy
    @Autowired
    private MessageController messageController;

    @Lazy
    @Autowired
    private CallBackQueryController callBackQueryController;
    @Lazy
    @Autowired
    private ValidationController validationController;

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;
    @Value("${user.admin}")
    private Long adminId;

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
        long id = 0;
        String tempText = "";
        boolean bln = false;
        if (update.hasCallbackQuery()) {
            id = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            if (update.getMessage().hasContact())
                tempText = String.valueOf(update.getMessage().getContact());
            else
                tempText = update.getMessage().getText();
            id = update.getMessage().getChatId();
        }

        if (!validationController.checkUSer(update) && !tempText.equals("/start")) {
            var remove = new ReplyKeyboardRemove();
            remove.setRemoveKeyboard(true);
            var sendMsg = new SendMessage();
            sendMsg.setText("Iltimos /start buyrug'ini bosing!\nПожалуйста, нажмите /start! ");
            sendMsg.setChatId(String.valueOf(id));
            sendMsg.setReplyMarkup(remove);
            sendMsg(sendMsg);
            return;
        }

        if (Objects.equals(id, adminId)) {
            bln = true;
        } else
            bln = validationController.mainController(update);

        if (bln) {
            if (update.hasMessage()) {
                messageController.messageController(update.getMessage());
            } else if (update.hasCallbackQuery())
                callBackQueryController
                        .callBackQueryController(update.getCallbackQuery());
            else if (update.getMessage().hasPhoto())
                USER_LIST.get(update.getCallbackQuery().getMessage().getChatId());
        }


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
            else if (obj instanceof SendVoice)
                execute((SendVoice) obj);
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
