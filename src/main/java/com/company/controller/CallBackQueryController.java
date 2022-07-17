package com.company.controller;


import com.company.dto.ComplaintsDTO;
import com.company.service.CallBackQueryService;
import com.company.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.LinkedList;
import java.util.List;

import static com.company.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.constants.ButtonName.*;
import static com.company.enums.Gender.FEMALE;
import static com.company.enums.Gender.MALE;
import static com.company.enums.LanguageCode.RU;
import static com.company.enums.LanguageCode.UZ;
import static com.company.enums.UserQuestionnaireStatus.DRUGS_LIST;
import static com.company.enums.UserQuestionnaireStatus.INSPECTION_PAPERS;
import static com.company.enums.UserStatus.COMPLAIN_FROM;
import static com.company.enums.UserStatus.COMPLAIN_INFO;
import static com.company.service.ComplaintsService.COMPLAINTS_LIST;

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
        else if (data.equals(UZ.name()))
            callBackQueryService.handleLangCodeUZ(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(RU.name()))
            callBackQueryService.handleLangCodeRU(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(MALE.name()))
            callBackQueryService.handleGenderMale(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(FEMALE.name()))
            callBackQueryService.handleGenderFemale(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(CONFIRM_UZ) || data.equals(CONFIRM_RU))
            callBackQueryService.handleCallBackConfirm(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(AGAIN_UZ))
            callBackQueryService.handleCallBackAgain(callbackQuery.getMessage(), callbackQuery.getFrom());
    }

    public void complaintFrom(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        if (data.equals(NEXT_UZ) || data.equals(NEXT_RU)) {
            var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
            user.setStartLenght(COMPLAINTS_LIST.size() / 2);
            user.setFinishLenght(1);
            USER_LIST.put(callbackQuery.getMessage().getChatId(), user);
            complaintsMessageController.complaintsForm(data, callbackQuery.getMessage());
        } else if (data.equals(BACK_UZ) || data.equals(BACK_RU)) {
            var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
            user.setStartLenght(0);
            user.setFinishLenght(2);
            USER_LIST.put(callbackQuery.getMessage().getChatId(), user);
            complaintsMessageController.complaintsForm(data, callbackQuery.getMessage());
        } else if (data.equals(STOP_UZ) || data.equals(STOP_RU)) {
            complaintsMessageController.result(callbackQuery.getMessage(), USER_LIST.get(callbackQuery.getMessage().getChatId()));
        } else if (data.equals(CONFIRM_UZ) || data.equals(CONFIRM_RU)) {
            var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
            user.setStatus(COMPLAIN_INFO);
            USER_LIST.put(callbackQuery.getMessage().getChatId(), user);
            callBackQueryService.startComplaintsInfoQuestionUz(callbackQuery.getMessage(), user);
        } else if (data.equals(AGAIN_UZ) || data.equals(AGAIN_RU)) {
            var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
            user.setStartLenght(0);
            user.setFinishLenght(2);
            USER_LIST.put(callbackQuery.getMessage().getChatId(), user);
            List<ComplaintsDTO> list = new LinkedList<>();
            USER_COMPLAINT.put(callbackQuery.getMessage().getChatId(), list);
            complaintsMessageController.complentsButtonList(callbackQuery.getMessage(), user, 0);
        } else {
            complaintsMessageController.complaintsForm(data, callbackQuery.getMessage());
        }
    }

    public void complaintsInfo(CallbackQuery callbackQuery) {
        var user = USER_LIST.get(callbackQuery.getMessage().getChatId());
        String data = callbackQuery.getData();
        if (data.equals(SKIP_UZ) || data.equals(SKIP_RU))
            callBackQueryService.result(callbackQuery.getMessage());
        else if (data.equals(STOP_UZ)&& user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
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
