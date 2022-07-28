package com.company.bot.controller;

import com.company.bot.service.AudioService;
import com.company.bot.service.PhotoServise;
import com.company.bot.util.button.InlineButtonUtil;
import com.company.bot.config.TelegramBotConfig;
import com.company.bot.dto.BotUsersDTO;
import com.company.bot.dto.ComplaintsDTO;
import com.company.bot.enums.UserQuestionnaireStatus;
import com.company.bot.repository.BotUsersRepository;
import com.company.bot.service.CallBackQueryService;
import com.company.bot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.company.bot.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.bot.config.TelegramBotConfig.USER_LIST;
import static com.company.bot.constants.ButtonName.*;
import static com.company.bot.enums.UserQuestionnaireStatus.*;
import static com.company.bot.enums.UserStatus.*;


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
    private final BotUsersRepository usersRepository;

    private final AudioService audioService;
    private final PhotoServise photoServise;

    @Value("${user.admin}")
    private Long adminId;


    public void messageController(Message message) {
        System.out.println("khshs");
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
        else if (user.getStatus().equals(ACTIVE) && text.equals(BACK_UZ)
                || user.getStatus().equals(ACTIVE) & text.equals(BACK_RU))
            callBackQueryService.backButton(message);
        else if (user.getStatus().equals(ACTIVE) && text.equals(CHANGE_LANG_RU)
                || user.getStatus().equals(ACTIVE) & text.equals(CHANGE_LANG_UZ))
            messageService.changeLanguage(message);
        else if (user.getStatus().equals(ACTIVE) && text.equals(MENU_UZ)
                || user.getStatus().equals(ACTIVE) & text.equals(MENU_RU))
            callBackQueryService.menu(message);
        else if (user.getStatus().equals(FILL_FORM) || text.equals(FILL_FORM_BTN_UZ) || text.equals(FILL_FORM_BTN_RU)) {
            user.setStatus(FILL_FORM);
            if (user.getQuestionnaireStatus().equals(DEFAULT))
                user.setQuestionnaireStatus(NAME);
            fillFrom(message, user);
        } else if (user.getStatus().equals(COMPLAIN_FROM)
                || text.equals(COMPLAINT_RU)
                || text.equals(COMPLAINT_UZ)) {

            if (text.equals(STOP_UZ) || text.equals(STOP_RU))
                complaintsMessageController.result(message, user);
            else {
                user.setStatus(COMPLAIN_FROM);
                USER_LIST.put(user.getTelegramId(), user);
                List<ComplaintsDTO> list = new LinkedList<>();
                USER_COMPLAINT.put(message.getChatId(), list);
                complaintsMessageController.complentsButtonList(message, user, 1);
            }
        } else if (user.getStatus().equals(COMPLAIN_INFO)) {
            if (message.hasVoice() && user.getQuestionnaireStatus().equals(COMPLAINTS_INFO_WRITE)) {
                audioService.getAudio(message);
            } else if (message.hasPhoto() && user.getQuestionnaireStatus().equals(DRUGS_LIST)) {
                photoServise.drugsPhotoSave(message);
            } else if (text.equals(STOP_UZ) && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                    || text.equals(STOP_RU) && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                    || text.equals(SKIP_RU) && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)
                    || text.equals(SKIP_UZ) && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)) {
                callBackQueryService.result(message);
            } else if (message.hasPhoto() && user.getQuestionnaireStatus().equals(INSPECTION_PAPERS)) {
                photoServise.inspectionPhotoSave(message);
            } else
                complaintInfo(message, user);

        }
    }


    private void start(Message message) {
        var user = usersRepository.findByTelegramId(message.getChatId());
        if (user.isEmpty())
            USER_LIST.put(message.getChatId(), new BotUsersDTO(message.getChatId()));
        else {
            var entity = user.get();

            var dto = new BotUsersDTO(entity.getTelegramId());
            dto.setLanguageCode(entity.getLanguageCode());
            dto.setName(entity.getName());
            dto.setBirthDate(dto.getBirthDate());
            dto.setGender(entity.getGender());
            dto.setHeight(entity.getHeight());
            dto.setPhone(entity.getPhone());
            dto.setWeight(entity.getWeight());
            dto.setStatus(ACTIVE);
            USER_LIST.put(message.getChatId(), dto);
            callBackQueryService.backButton(message);
            return;
        }

        var remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        var sendMessage1 = new SendMessage();
        sendMessage1.setReplyMarkup(remove);
        sendMessage1.setChatId(String.valueOf(message.getChatId()));
        sendMessage1.setText("...");

        int id = 0;

        try {
            id = telegramBotConfig.execute(sendMessage1).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        var delete = new DeleteMessage();
        delete.setChatId(String.valueOf(message.getChatId()));
        delete.setMessageId(id);
        telegramBotConfig.sendMsg(delete);

        var sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(remove);
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText("Iltimos, tilni tanlang. / Пожалуйста, выберите язык.");
        sendMessage.setReplyMarkup(InlineButtonUtil.languageButtons());
        telegramBotConfig.sendMsg(sendMessage);
    }

    public void fillFrom(Message message, BotUsersDTO user) {
        var qStatus = user.getQuestionnaireStatus();
        String text="";

        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        if (message.hasContact())
            text = String.valueOf(message.getContact());
        else
            text= message.getText();

        if (Objects.equals(SKIP_UZ, text) || Objects.equals(text,SKIP_RU)) {
            System.out.println("if");
            fillFromTemp(qStatus, message, user, sendMessage);
            return;
        }

        switch (qStatus) {
            case NAME -> messageService.name(message, user, sendMessage);
            case SURNAME -> messageService.surname(message, user, sendMessage);
            case BIRTH_DATE -> messageService.birthDate(message, user, sendMessage);
            case GENDER -> messageService.gender(message, user, sendMessage);
            case HEIGHT -> messageService.height(message, user, sendMessage);
            case WEIGHT -> messageService.weight(message, user, sendMessage);
            case PHONE -> messageService.phone(message, user, sendMessage);
            case BLOOD_PRESSURE -> messageService.bloodPressure(message, user, sendMessage, text);
            case HEART_BEAT -> messageService.heartBeats(message, user, sendMessage, text);
            case TEMPERATURE -> messageService.tempratura(message, user, sendMessage, text);
            case DIABETES -> messageService.diabeats(message, user, sendMessage, text);
        }
    }

    public void fillFromTemp(UserQuestionnaireStatus qStatus, Message message, BotUsersDTO user, SendMessage sendMessage) {
        switch (qStatus) {
            case BLOOD_PRESSURE -> messageService.bloodPressure(message, user, sendMessage, null);
            case HEART_BEAT -> messageService.heartBeats(message, user, sendMessage, null);
            case TEMPERATURE -> messageService.tempratura(message, user, sendMessage, null);
            case DIABETES -> messageService.diabeats(message, user, sendMessage, null);
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
