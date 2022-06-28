package com.company.controller;


import com.company.enums.ButtonName;
import com.company.enums.Gender;
import com.company.service.CallBackQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.company.enums.ButtonName.*;
import static com.company.enums.Gender.FEMALE;
import static com.company.enums.Gender.MALE;
import static com.company.enums.LanguageCode.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CallBackQueryController {
    @Lazy
    private final CallBackQueryService callBackQueryService;

    public void callBackQueryController(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        if (data.equals(UZ.name()))
            callBackQueryService.handleLangCodeUZ(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(RU.name()))
            callBackQueryService.handleLangCodeRU(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(MALE.name()))
            callBackQueryService.handleGenderMale(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(FEMALE.name()))
            callBackQueryService.handleGenderFemale(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(CONFIRM_UZ)|| data.equals(CONFIRM_RU))
            callBackQueryService.handleCallBackConfirm(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(AGAIN_UZ))
            callBackQueryService.handleCallBackAgain(callbackQuery.getMessage(), callbackQuery.getFrom());


    }
}
