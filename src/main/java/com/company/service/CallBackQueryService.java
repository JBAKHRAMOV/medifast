package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.entity.BotUsersEntity;
import com.company.entity.QuestionnaireEntity;
import com.company.enums.Gender;
import com.company.enums.UserQuestionnaireStatus;
import com.company.enums.UserStatus;
import com.company.util.button.ButtonUtil;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.company.enums.LanguageCode.RU;
import static com.company.enums.LanguageCode.UZ;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallBackQueryService {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final BotUsersService botUsersService;
    private final MessageService messageService;
    private final QuestionnaireService questionnaireService;

    public void handleLangCodeUZ(Message message, User user) {
        var entity = botUsersService.getByTelegramId(user.getId());
        entity.setLanguageCode(UZ);
        botUsersService.saveUser(entity);

        var deleteMessage = new DeleteMessage(String.valueOf(message.getChatId()), message.getMessageId());

        telegramBotConfig.sendMsg(deleteMessage);

        var sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Asosiy bo'limga xush keldingiz 😊");
        sendMessage.setReplyMarkup(ButtonUtil.keyboard(UZ));

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void handleLangCodeRU(Message message, User user) {
        var entity = botUsersService.getByTelegramId(user.getId());
        entity.setLanguageCode(RU);
        botUsersService.saveUser(entity);

        var deleteMessage = new DeleteMessage(String.valueOf(message.getChatId()), message.getMessageId());

        telegramBotConfig.sendMsg(deleteMessage);

        var sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Добро пожаловать в основной раздел \uD83D\uDE0A");
        sendMessage.setReplyMarkup(ButtonUtil.keyboard(RU));

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void handleGenderMale(Message message, User user) {
        var entity = questionnaireService.getByTelegramId(user.getId());

        entity.setGender(Gender.MALE);
        entity.setStatus(UserQuestionnaireStatus.HEIGHT);

        questionnaireService.save(entity);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setText("✅ Muvaffaqiyatli / ✅ Успешный");
        telegramBotConfig.sendMsg(editMessageText);


        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));

        var botUsers = botUsersService.getByTelegramId(user.getId());
        switch (botUsers.getLanguageCode()) {
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
        var entity = questionnaireService.getByTelegramId(user.getId());

        entity.setGender(Gender.FEMALE);
        entity.setStatus(UserQuestionnaireStatus.HEIGHT);

        questionnaireService.save(entity);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setText("✅ Muvaffaqiyatli / ✅ Успешный");
        telegramBotConfig.sendMsg(editMessageText);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));

        var botUsers = botUsersService.getByTelegramId(user.getId());
        switch (botUsers.getLanguageCode()) {
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
        var botUsers = botUsersService.getByTelegramId(user.getId());
        var questionnaire = questionnaireService.getByTelegramId(user.getId());

        toBotUsers(botUsers, questionnaire);

        var editMessageText = new EditMessageText();

        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setText("Botni shu qismigacha bo'lgan kodlar yozib bo'lindi!\n Qolgan qismi yaqin kunlarda chiqadi!");
        telegramBotConfig.sendMsg(editMessageText);
    }

    public void handleCallBackAgain(Message message, User user) {
        var botUsers = botUsersService.getByTelegramId(user.getId());
        var questionnaire = questionnaireService.getByTelegramId(user.getId());

        botUsers.setStatus(UserStatus.NOT_ACTIVE);
        questionnaire.setStatus(UserQuestionnaireStatus.NAME);

        questionnaireService.save(questionnaire);
        botUsersService.saveUser(botUsers);

        var deleteMessage = new DeleteMessage();

        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(String.valueOf(message.getChatId()));

        telegramBotConfig.sendMsg(deleteMessage);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        switch (botUsers.getLanguageCode()) {
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

    private void toBotUsers(BotUsersEntity botUsers, QuestionnaireEntity questionnaire) {
        botUsers.setStatus(UserStatus.ACTIVE);
        botUsers.setGender(questionnaire.getGender());
        botUsers.setBirthDate(questionnaire.getBirthDate());
        botUsers.setHeight(questionnaire.getHeight());
        botUsers.setWeight(questionnaire.getWeight());
        botUsers.setPhone(questionnaire.getPhone());
        botUsers.setName(questionnaire.getName());
        botUsers.setSurname(questionnaire.getSurname());

        botUsersService.saveUser(botUsers);
        questionnaireService.delete(questionnaire);
    }

}
