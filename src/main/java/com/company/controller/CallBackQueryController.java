package com.company.controller;


import com.company.enums.Gender;
import com.company.service.CallBackQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

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
        else if (data.equals(Gender.MALE.name()))
            callBackQueryService.handleGenderMale(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals(Gender.FEMALE.name()))
            callBackQueryService.handleGenderFemale(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals("confirm"))
            callBackQueryService.handleCallBackConfirm(callbackQuery.getMessage(), callbackQuery.getFrom());
        else if (data.equals("again"))
            callBackQueryService.handleCallBackAgain(callbackQuery.getMessage(), callbackQuery.getFrom());


    }
}
