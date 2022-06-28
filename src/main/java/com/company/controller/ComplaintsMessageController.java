package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.service.ComplaintsService;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.company.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.service.ComplaintsService.COMPLAINTS_LIST;

public class ComplaintsMessageController {

    public void test(Message message, BotUsersDTO user) {
        var complaint = message.getDate();

        var lang = user.getLanguageCode();
        switch (lang) {
            case UZ -> {
                for (int i = 0; i < COMPLAINTS_LIST.size(); i++) {
                    var complaintsList = USER_COMPLAINT.get(message.getChatId());


                }
            }
        }
    }
}
