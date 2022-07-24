package com.company.controller;

import com.company.dto.BotUsersDTO;
import com.company.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.company.config.TelegramBotConfig.USER_LIST;

@Component
@RequiredArgsConstructor
public class ValidationController {

    @Lazy
    private final ValidationService validationService;

    public boolean mainController(Update update) {
        var bln = false;

        if (update.hasMessage())
            if (update.getMessage().hasText())
                if (update.getMessage().getText().equals("/start"))
                    return true;
        var user = new BotUsersDTO();
        if (update.hasCallbackQuery()) {
            user = USER_LIST.get(update.getCallbackQuery().getMessage().getChatId());
        } else if (update.hasMessage()) {
            user = USER_LIST.get(update.getMessage().getChatId());
        }
        System.out.println(user.getStatus() + "  " + user.getQuestionnaireStatus());

        switch (user.getStatus()) {
            case ACTIVE -> bln = validationService.active(update);
            case FILL_FORM -> {
                switch (user.getQuestionnaireStatus()) {
                    case WEIGHT, BIRTH_DATE, HEIGHT, SURNAME, NAME ,BLOOD_PRESSURE, HEART_BEAT, TEMPERATURE, DIABETES-> bln = validationService.fillFormSome(update);
                    case PHONE -> bln = validationService.fillFormPhone(update);
                    case GENDER -> bln = validationService.fillFormGender(update);
                    case DEFAULT -> bln = validationService.fillFormDefault(update);
                }
            }
            case NOT_ACTIVE -> bln = validationService.notActive(update);
            case COMPLAIN_FROM -> bln = validationService.complainFrom(update);
            case COMPLAIN_INFO -> {
                switch (user.getQuestionnaireStatus()) {
                    case COMPLAINTS_INFO_WRITE -> bln = validationService.complainFromInfoInfoWrite(update);
                    case COMPLAINTS_STARTED_TIME, DISEASES_LIST -> bln = validationService.complainFromInfoSome(update);
                    case DRUGS_LIST -> bln = validationService.complainFromInfoDrugsList(update);
                    case CIGARETTE -> bln = validationService.complainFromInfoCigareta(update);
                    case INSPECTION_PAPERS -> bln = validationService.complainFromInfoinpection(update);
                }
            }
            case CHANGE_LANG -> bln = validationService.changeLang(update);
        }
        return bln;
    }

    public boolean checkUSer(Update update) {
        if (update.hasCallbackQuery())
            return validationService.checkUser(update.getCallbackQuery().getMessage().getChatId());
        else if (update.hasMessage())
            return validationService.checkUser(update.getMessage().getChatId());
        return false;
    }
}

