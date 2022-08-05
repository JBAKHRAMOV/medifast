package com.company.bot.controller;

import com.company.bot.service.ComplaintsMessageService;
import com.company.bot.service.ComplaintsService;
import com.company.bot.util.button.InlineButtonUtil;
import com.company.bot.config.TelegramBotConfig;
import com.company.bot.dto.BotUsersDTO;
import com.company.bot.dto.ComplaintsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.company.bot.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.bot.config.TelegramBotConfig.USER_LIST;
import static com.company.bot.constants.ButtonName.*;

@RequiredArgsConstructor
@Component
public class ComplaintsMessageController {

    private final TelegramBotConfig telegramBotConfig;

    private final ComplaintsMessageService complaintsMessageService;

    public void complaintsForm(String text, Message message) {

        var list = USER_COMPLAINT.get(message.getChatId());

        ComplaintsDTO dto = null;
        for (var complaint : ComplaintsService.COMPLAINTS_LIST) {
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
}
