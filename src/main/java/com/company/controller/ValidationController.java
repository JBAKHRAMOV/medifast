package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

import static com.company.config.TelegramBotConfig.USER_LIST;

@Component
@RequiredArgsConstructor
public class ValidationController {

    @Lazy
    private final ValidationService validationService;

    @Value("${user.admin}")
    private String adminId;

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
            case ACTIVE -> bln = validationService.active(update);// TODO: 21/07/22 text button
            case FILL_FORM -> {
                switch (user.getQuestionnaireStatus()) {
                    case WEIGHT, BIRTH_DATE, HEIGHT, SURNAME, NAME ->
                            bln = validationService.fillFormSome(update);// TODO: 21/07/22 faqat harf
                    case PHONE -> bln = validationService.fillFormPhone(update);
                    case GENDER ->
                            bln = validationService.fillFormGender(update);// TODO: 21/07/22 harf yoki callback query
                    case DEFAULT -> bln = validationService.fillFormDefault(update);// TODO: 21/07/22 callbackquery
                }
            }
            case NOT_ACTIVE ->
                    bln = validationService.notActive(update);// TODO: 21/07/22 ozbekcha ruscha callbek query yani til tanlash va anketa toldirish bot haqida malumot ;
            case COMPLAIN_FROM -> bln = validationService.complainFrom(update);// TODO: 21/07/22 callback query
            case COMPLAIN_INFO -> {
                switch (user.getQuestionnaireStatus()) {
                    case COMPLAINTS_INFO_WRITE ->
                            bln = validationService.complainFromInfoInfoWrite(update);// TODO: 21/07/22 audio va harf ;
                    case COMPLAINTS_STARTED_TIME, DISEASES_LIST ->
                            bln = validationService.complainFromInfoSome(update); // TODO: 21/07/22 harf
                    case DRUGS_LIST ->
                            bln = validationService.complainFromInfoDrugsList(update);// TODO: 21/07/22 rasm yoki harf
                    case CIGARETTE ->
                            bln = validationService.complainFromInfoCigareta(update);// TODO: 21/07/22 callback
                    case INSPECTION_PAPERS ->
                            bln = validationService.complainFromInfoinpection(update);// TODO: 21/07/22 rasm yoki button message
                }
            }
            case CHANGE_LANG -> bln = validationService.changeLang(update);
        }
        return bln;
    }
}

