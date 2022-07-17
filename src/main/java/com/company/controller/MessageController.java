package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.dto.ComplaintsDTO;
import com.company.service.AudioService;
import com.company.service.CallBackQueryService;
import com.company.service.MessageService;
import com.company.service.PhotoServise;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.company.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.constants.ButtonName.*;
import static com.company.enums.UserQuestionnaireStatus.*;
import static com.company.enums.UserStatus.*;


@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final MessageService messageService;
    private final ComplaintsMessageController complaintsMessageController;
    private final AdminController adminController;
    private final CallBackQueryService callBackQueryService;

    private final AudioService audioService;
    private final PhotoServise photoServise;

    @Value("${user.admin}")
    private Long adminId;


    public void messageController(Message message) {
        var text = "";
        var user = USER_LIST.get(message.getChatId());

        if (Objects.equals(adminId, message.getFrom().getId())) {
            adminController.messageController(message);
            return;
        }
        if (text == null) {
            return;
        }

        if (message.hasText())
            text = message.getText();

        if (message.hasContact()) text = message.getContact().getPhoneNumber();

        if (text.equals("/start"))
            start(message);

        else if (user.getStatus().equals(FILL_FORM) || text.equals(FILL_FORM_BTN_UZ) || text.equals(FILL_FORM_BTN_RU)) {
            user.setStatus(FILL_FORM);
            fillFrom(message, user);
        } else if (user.getStatus().equals(COMPLAIN_FROM)
                || text.equals(COMPLAINT_RU)
                || text.equals(COMPLAINT_UZ)) {

            if (text.equals(STOP_UZ) || text.equals(STOP_RU))
                complaintsMessageController.result(message, user);
            else {
                user.setStatus(COMPLAIN_FROM);
                List<ComplaintsDTO> list = new LinkedList<>();
                USER_COMPLAINT.put(message.getChatId(), list);
                complaintsMessageController.complentsButtonList(message, user, 1);
            }
        } else if (user.getStatus().equals(COMPLAIN_INFO)) {
            if (message.hasVoice()&& user.getQuestionnaireStatus().equals(COMPLAINTS_INFO_WRITE)) {
                audioService.getAudio(message);
            }else if (message.hasPhoto()&& user.getQuestionnaireStatus().equals(DRUGS_LIST))
                photoServise.drugsPhotoSave(message);
            else if (message.hasPhoto()&& user.getQuestionnaireStatus().equals(DRUGS_LIST)) {
                photoServise.drugsPhotoSave(message);
            } else if (text.equals(STOP_UZ)&& user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                    || text.equals(STOP_RU)&& user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                    || text.equals(SKIP_RU)&& user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                    || text.equals(SKIP_UZ)&& user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)) {
                callBackQueryService.result(message);
            } else if (message.hasPhoto() && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)) {
                photoServise.inspectionPhotoSave(message);
            } else
                complaintInfo(message, user);

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

        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());


        switch (qStatus) {
            case NAME -> messageService.name(message, user, sendMessage);
            case SURNAME -> messageService.surname(message, user, sendMessage);
            case BIRTH_DATE -> messageService.birthDate(message, user, sendMessage);
            case GENDER -> messageService.gender(message, user, sendMessage);
            case HEIGHT -> messageService.height(message, user, sendMessage);
            case WEIGHT -> messageService.weight(message, user, sendMessage);
            case PHONE -> messageService.phone(message, user, sendMessage);
        }
    }


    public void complaintInfo(Message message, BotUsersDTO user) {
        var qStatus = user.getQuestionnaireStatus();

        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());

        switch (qStatus) {
            case COMPLAINTS_INFO_WRITE -> messageService.complaintsInfoWrite(message, user);
            case COMPLAINTS_STARTED_TIME -> messageService.complaintsStartedDate(message, user);
            case DRUGS_LIST -> messageService.drugsList(message, user);
            case DISEASES_LIST -> messageService.disiasesList(message, user);
        }
    }
}
