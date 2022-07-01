package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.entity.BotUsersEntity;
import com.company.enums.Gender;
import com.company.enums.UserQuestionnaireStatus;
import com.company.enums.UserStatus;
import com.company.util.button.ButtonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.enums.LanguageCode.RU;
import static com.company.enums.LanguageCode.UZ;
import static com.company.enums.UserStatus.ACTIVE;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallBackQueryService {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final BotUsersService botUsersService;

    public void handleLangCodeUZ(Message message, User user) {
        var dto = USER_LIST.get(user.getId());

        dto.setLanguageCode(UZ);
        USER_LIST.put(user.getId(), dto);
        var deleteMessage = new DeleteMessage(String.valueOf(message.getChatId()), message.getMessageId());

        telegramBotConfig.sendMsg(deleteMessage);

        var sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Asosiy bo'limga xush keldingiz 😊");
        sendMessage.setReplyMarkup(ButtonUtil.keyboard(UZ));

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void handleLangCodeRU(Message message, User user) {
        var dto = USER_LIST.get(user.getId());
        dto.setLanguageCode(RU);
        USER_LIST.put(user.getId(), dto);

        var deleteMessage = new DeleteMessage(String.valueOf(message.getChatId()), message.getMessageId());

        telegramBotConfig.sendMsg(deleteMessage);

        var sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Добро пожаловать в основной раздел \uD83D\uDE0A");
        sendMessage.setReplyMarkup(ButtonUtil.keyboard(RU));

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void handleGenderMale(Message message, User user) {
        var dto = USER_LIST.get(user.getId());

        dto.setGender(Gender.MALE);
        dto.setQuestionnaireStatus(UserQuestionnaireStatus.HEIGHT);

        USER_LIST.put(user.getId(), dto);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setText("✅ Muvaffaqiyatli / ✅ Успешный");
        telegramBotConfig.sendMsg(editMessageText);


        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));

        switch (dto.getLanguageCode()) {
            case UZ -> {
                sendMessage.setText("Bo'yingiz uzunligini kiriting.\nNamuna: (175-sm) ");
                telegramBotConfig.sendMsg(sendMessage);
            }
            case RU -> {
                sendMessage.setText("укажите свой рост. \nПример: (175 см)");
                telegramBotConfig.sendMsg(sendMessage);
            }
        }
    }

    public void handleGenderFemale(Message message, User user) {
        var dto = USER_LIST.get(user.getId());

        dto.setGender(Gender.FEMALE);
        dto.setQuestionnaireStatus(UserQuestionnaireStatus.HEIGHT);

        USER_LIST.put(user.getId(), dto);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setText("✅ Muvaffaqiyatli / ✅ Успешный");
        telegramBotConfig.sendMsg(editMessageText);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));

        switch (dto.getLanguageCode()) {
            case UZ -> {
                sendMessage.setText("Bo'yingiz uzunligini kiriting.\nNamuna: (175-sm) ");
                telegramBotConfig.sendMsg(sendMessage);
            }
            case RU -> {
                sendMessage.setText("укажите свой рост. \nПример: (175 см)");
                telegramBotConfig.sendMsg(sendMessage);
            }
        }
    }

    public void handleCallBackConfirm(Message message, User user) {

        var dto = USER_LIST.get(user.getId());
        dto.setStatus(ACTIVE);
        USER_LIST.put(message.getChatId(), dto);

        save(dto);

        var editMessageText = new SendMessage();

        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setText("Botni shu qismigacha bo'lgan kodlar yozib bo'lindi!\n Qolgan qismi yaqin kunlarda chiqadi!");
        editMessageText.setReplyMarkup(ButtonUtil.complaintsMenu(UZ));
        telegramBotConfig.sendMsg(editMessageText);
    }

    public void handleCallBackAgain(Message message, User user) {

        var dto = USER_LIST.get(user.getId());

        dto.setStatus(UserStatus.NOT_ACTIVE);
        dto.setQuestionnaireStatus(UserQuestionnaireStatus.NAME);

        USER_LIST.put(message.getChatId(), dto);

        var deleteMessage = new DeleteMessage();

        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(String.valueOf(message.getChatId()));

        telegramBotConfig.sendMsg(deleteMessage);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        switch (dto.getLanguageCode()) {
            case UZ -> {
                sendMessage.setText("Iltimos, yana qaytadan ma'lumotlaringizni kiritib chiqing");
                sendMessage.setReplyMarkup(ButtonUtil.fillFormButton(UZ));
            }
            case RU -> {
                sendMessage.setText("Пожалуйста, введите ваши данные еще раз");
                sendMessage.setReplyMarkup(ButtonUtil.fillFormButton(RU));
            }
        }
        telegramBotConfig.sendMsg(sendMessage);
    }

    private void save(BotUsersDTO dto) {
        var entity = new BotUsersEntity();
        entity.setStatus(ACTIVE);
        entity.setGender(dto.getGender());
        entity.setBirthDate(dto.getBirthDate());
        entity.setHeight(dto.getHeight());
        entity.setWeight(dto.getWeight());
        entity.setPhone(dto.getPhone());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        botUsersService.saveUser(entity);
    }

}
