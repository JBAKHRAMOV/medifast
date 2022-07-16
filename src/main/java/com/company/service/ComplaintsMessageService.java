package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.enums.LanguageCode;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.company.config.TelegramBotConfig.USER_LIST;

@Component
@RequiredArgsConstructor
public class ComplaintsMessageService {

    private final TelegramBotConfig telegramBotConfig;

    public void buttonList(Message message, LanguageCode lang, Integer integer) {

        var remove = new ReplyKeyboardRemove();

        remove.setRemoveKeyboard(true);

        if (integer == 0) {
            var deleteMsg = new DeleteMessage();
            deleteMsg.setMessageId(message.getMessageId());
            deleteMsg.setChatId(String.valueOf(message.getChatId()));
            telegramBotConfig.sendMsg(deleteMsg);
            deleteMsg.setMessageId(message.getMessageId() - 1);
            telegramBotConfig.sendMsg(deleteMsg);
        }
        var sendMessage = new SendMessage();

        sendMessage.setReplyMarkup(remove);
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText("Tayyorlanmoqda...");
        Message message1=null;
        try {
            message1=telegramBotConfig.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        var deleteMsg = new DeleteMessage();
        deleteMsg.setMessageId(message1.getMessageId());
        deleteMsg.setChatId(String.valueOf(message1.getChatId()));
        telegramBotConfig.sendMsg(deleteMsg);

        sendMessage.setReplyMarkup(InlineButtonUtil.complaintButtonList(lang));
        sendMessage.setText("O'zingizga tegishli bo'limlarni belgilang");
        telegramBotConfig.sendMsg(sendMessage);



    }

    public void result(Message message, LanguageCode lang) {
        var sendMessage = new SendMessage();

        var str = new StringBuilder();
        var list = TelegramBotConfig.USER_COMPLAINT.get(message.getChatId());
        if (!list.isEmpty()) {
            switch (lang) {
                case UZ -> {
                    for (var complaits : list) {
                        str.append(complaits.getNameUz()).append("\n");
                    }
                }
                case RU -> {
                    for (var complaits : list) {
                        str.append(complaits.getNameRu()).append("\n");
                    }
                }
            }
        } else if (USER_LIST.get(message.getChatId()).getLanguageCode().equals(LanguageCode.UZ))
            str.append("Siz hech qanday shikoyat belgilamagansiz!");
        else
            str.append("Вы не отметили жалоб!");

        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setReplyMarkup(InlineButtonUtil.confirmComplints(lang));
        sendMessage.setText(str.toString());
        telegramBotConfig.sendMsg(sendMessage);
    }

}
