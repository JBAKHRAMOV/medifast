package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.dto.ComplaintsDTO;
import com.company.service.ComplaintsMessageService;
import com.company.service.ComplaintsService;
import com.company.util.button.ButtonUtil;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.LinkedList;
import java.util.List;

import static com.company.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.enums.UserStatus.COMPLAIN_FROM;
import static com.company.service.ComplaintsService.COMPLAINTS_LIST;

@RequiredArgsConstructor
@Component
public class ComplaintsMessageController {

    private final TelegramBotConfig telegramBotConfig;

    private final ComplaintsMessageService complaintsMessageService;

    public  void complaintsForm(String text, Message message){
        System.out.println(message.getChatId());
        var lis=USER_COMPLAINT.get(message.getChatId());
        System.out.println(USER_COMPLAINT);
        for (var complaint: COMPLAINTS_LIST) {
            if (text.equals(complaint.getKey())){
                lis.add(complaint);
                System.out.println(complaint);
                USER_COMPLAINT.put(message.getChatId(), lis);
                break;
            }
        }
        System.out.println(USER_COMPLAINT);

        var lang=USER_LIST.get(message.getChatId()).getLanguageCode();

        var edit=new DeleteMessage();
        edit.setChatId(String.valueOf(message.getChatId()));
        edit.setMessageId(message.getMessageId());
        telegramBotConfig.sendMsg(edit);

        var sendMessage=new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setReplyMarkup(InlineButtonUtil.complaintButtonListSendAgain(lang, message.getChatId()));
        sendMessage.setText("belgilab bolganingizdan song tugatish tugamsini bosing");
        telegramBotConfig.sendMsg(sendMessage);

    }

    public  void complentsButtonList(Message message, BotUsersDTO user, Integer integer){
       complaintsMessageService.buttonList(message, user.getLanguageCode(), integer);
    }

    public  void result(Message message, BotUsersDTO user){
        complaintsMessageService.result(message, user.getLanguageCode());
    }

    public void nextComplaint(Message message){

    }
}
