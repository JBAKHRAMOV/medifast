package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.dto.ComplaintsDTO;
import com.company.service.ComplaintsMessageService;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.company.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.constants.ButtonName.*;
import static com.company.service.ComplaintsService.COMPLAINTS_LIST;

@RequiredArgsConstructor
@Component
public class ComplaintsMessageController {

    private final TelegramBotConfig telegramBotConfig;

    private final ComplaintsMessageService complaintsMessageService;

    public void complaintsForm(String text, Message message) {

        var list = USER_COMPLAINT.get(message.getChatId());

        ComplaintsDTO dto = null;
        for (var complaint : COMPLAINTS_LIST) {
            if (complaint.getKey().equals(text)) {
                dto = complaint;
                break;
            }
        }

        if (!text.equals(NEXT_RU) && !text.equals(NEXT_UZ)
                && !text.equals(BACK_RU) && !text.equals(BACK_UZ)) {
            if (!list.contains(dto))
                list.add(dto);
            else
                list.remove(dto);
        }

        USER_COMPLAINT.put(message.getChatId(), list);

        var lang = USER_LIST.get(message.getChatId()).getLanguageCode();

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setReplyMarkup(InlineButtonUtil.complaintButtonListSendAgain(lang, message.getChatId(), text));
        editMessageText.setText("Belgilab bo'lganingizdan so'ng tugatish tugmasini bosing");

        telegramBotConfig.sendMsg(editMessageText);

    }

    public void complentsButtonList(Message message, BotUsersDTO user, Integer integer) {
        complaintsMessageService.buttonList(message, user.getLanguageCode(), integer);
    }

    public void result(Message message, BotUsersDTO user) {
        complaintsMessageService.result(message, user.getLanguageCode());
    }

    public void nextComplaint(Message message) {

    }
}
