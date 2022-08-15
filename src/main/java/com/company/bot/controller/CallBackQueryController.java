package com.company.bot.controller;


import com.company.bot.dto.ComplaintsDTO;
import com.company.bot.service.CallBackQueryService;
import com.company.bot.service.ComplaintsService;
import com.company.bot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.LinkedList;
import java.util.List;

import static com.company.bot.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.bot.config.TelegramBotConfig.USER_LIST;
import static com.company.bot.constants.ButtonName.*;
import static com.company.bot.enums.Gender.FEMALE;
import static com.company.bot.enums.Gender.MALE;
import static com.company.bot.enums.LanguageCode.RU;
import static com.company.bot.enums.LanguageCode.UZ;
import static com.company.bot.enums.UserQuestionnaireStatus.DRUGS_LIST;
import static com.company.bot.enums.UserQuestionnaireStatus.INSPECTION_PAPERS;
import static com.company.bot.enums.UserStatus.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CallBackQueryController {
    @Lazy
    private final CallBackQueryService callBackQueryService;

    private final ComplaintsMessageController complaintsMessageController;
    private final MessageService messageService;

    public void callBackQueryController(CallbackQuery callbackQuery) {
        var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
        String data = callbackQuery.getData();


        if (user.getStatus().equals(COMPLAIN_FROM)) complaintFrom(callbackQuery);
        else if (user.getStatus().equals(COMPLAIN_INFO)) complaintsInfo(callbackQuery);
        else if (user.getStatus().equals(CHANGE_LANG))
            callBackQueryService.changeLang(callbackQuery.getMessage(), data);
        else if (data.equals(UZ.name()))
            callBackQueryService.handleLangCodeUZ(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(RU.name()))
            callBackQueryService.handleLangCodeRU(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(MALE.name()))
            callBackQueryService.handleGenderMale(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(FEMALE.name()))
            callBackQueryService.handleGenderFemale(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(CONFIRM_UZ) || data.equals(CONFIRM_RU))
            callBackQueryService.handleCallBackConfirm(callbackQuery.getMessage());
        else if (data.equals(AGAIN_UZ)|| data.equals(AGAIN_RU))
            callBackQueryService.handleCallBackAgain(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (user.getStatus().equals(ACTIVE)&& data.equals(AGAIN_DATA_RU)
        ||user.getStatus().equals(ACTIVE)&& data.equals(AGAIN_DATA_UZ)) {
            var sendMsg=new SendMessage();
            sendMsg.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
            user.setStatus(FILL_FORM);
            messageService.name(callbackQuery.getMessage(), user, sendMsg);
        }


    }

    public void complaintFrom(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        switch (data) {
            case NEXT_UZ, NEXT_RU -> {
                var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
                user.setStartLength(ComplaintsService.COMPLAINTS_LIST.size() / 2);
                user.setFinishLength(1);
                USER_LIST.put(callbackQuery.getMessage().getChatId(), user);
                complaintsMessageController.complaintsForm(data, callbackQuery.getMessage());
            }
            case BACK_UZ, BACK_RU -> {
                var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
                user.setStartLength(0);
                user.setFinishLength(2);
                USER_LIST.put(callbackQuery.getMessage().getChatId(), user);
                complaintsMessageController.complaintsForm(data, callbackQuery.getMessage());
            }
            case STOP_UZ, STOP_RU ->
                    complaintsMessageController.result(callbackQuery.getMessage(), USER_LIST.get(callbackQuery.getMessage().getChatId()));
            case CONFIRM_UZ, CONFIRM_RU -> {
                var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
                user.setStatus(COMPLAIN_INFO);
                USER_LIST.put(callbackQuery.getMessage().getChatId(), user);
                callBackQueryService.startComplaintsInfoQuestionUz(callbackQuery.getMessage(), user);
            }
            case AGAIN_UZ, AGAIN_RU -> {
                var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
                user.setStartLength(0);
                user.setFinishLength(2);
                USER_LIST.put(callbackQuery.getMessage().getChatId(), user);
                List<ComplaintsDTO> list = new LinkedList<>();
                USER_COMPLAINT.put(callbackQuery.getMessage().getChatId(), list);
                complaintsMessageController.complentsButtonList(callbackQuery.getMessage(), user, 0);
            }
            default -> complaintsMessageController.complaintsForm(data, callbackQuery.getMessage());
        }
    }

    public void complaintsInfo(CallbackQuery callbackQuery) {
        var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
        String data = callbackQuery.getData();
        if (data.equals(SKIP_UZ) || data.equals(SKIP_RU)) {
            callBackQueryService.result(callbackQuery.getMessage());
        } else if (data.equals(STOP_UZ)&& user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                || data.equals(STOP_RU)&& user.getQuestionnaireStatus().equals(INSPECTION_PAPERS))
            callBackQueryService.result(callbackQuery.getMessage());
        else if (data.equals(STOP_RU) && user.getQuestionnaireStatus().equals(DRUGS_LIST)
                || data.equals(STOP_UZ) && user.getQuestionnaireStatus().equals(DRUGS_LIST))
            messageService.drugsList(callbackQuery.getMessage(), user);
        else if (data.equals(CONFIRM_RU) && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                || data.equals(CONFIRM_UZ) && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS))
            callBackQueryService.confirm(callbackQuery.getMessage());
        else if (data.equals(AGAIN_UZ) && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                || data.equals(AGAIN_RU) && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)){
            callBackQueryService.again(callbackQuery.getMessage());
            callBackQueryService.startComplaintsInfoQuestionUz(callbackQuery.getMessage(), user);
        }
        else callBackQueryService.cigarette(callbackQuery);
    }
}
