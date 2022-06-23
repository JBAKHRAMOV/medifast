package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.entity.QuestionnaireEntity;
import com.company.enums.Gender;
import com.company.repository.QuestionnaireRepository;
import com.company.util.DateUtil;
import com.company.util.button.ButtonUtil;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.time.DateTimeException;
import java.time.LocalDate;

import static com.company.enums.LanguageCode.UZ;
import static com.company.enums.UserQuestionnaireStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireService {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final BotUsersService botUsersService;
    private final QuestionnaireRepository questionnaireRepository;

    public void save(QuestionnaireEntity entity) {
        questionnaireRepository.save(entity);
    }

    public void create(Message message, QuestionnaireEntity entity) {

        var user = message.getFrom();

        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));


        var botUsers = botUsersService.getByTelegramId(user.getId());
        switch (botUsers.getLanguageCode()) {
            case UZ -> {
                if (entity.getStatus() == NAME) {
                    var remove = new ReplyKeyboardRemove();
                    remove.setRemoveKeyboard(true);

                    sendMessage.setText("Iltimos, ismingizni kiriting.");
                    sendMessage.setReplyMarkup(remove);
                    telegramBotConfig.sendMsg(sendMessage);

                    entity.setStatus(SURNAME);

                    questionnaireRepository.save(entity);
                } else if (entity.getStatus() == SURNAME) {
                    sendMessage.setText("Iltimos, familyangizni kiriting.");

                    telegramBotConfig.sendMsg(sendMessage);

                    entity.setName(message.getText());
                    entity.setStatus(BIRTH_DATE);

                    questionnaireRepository.save(entity);
                } else if (entity.getStatus() == BIRTH_DATE) {
                    sendMessage.setText("Iltimos, tug'ilgan kuningizni kiriting. \nNamuna (24.11.2003)");
                    telegramBotConfig.sendMsg(sendMessage);

                    entity.setSurname(message.getText());
                    entity.setStatus(GENDER);

                    questionnaireRepository.save(entity);
                } else if (entity.getStatus() == GENDER) {
                    try {
                        LocalDate localDate = DateUtil.stringToDate(message.getText());

                        entity.setBirthDate(localDate);
                        entity.setStatus(GENDER);

                        questionnaireRepository.save(entity);

                        sendMessage.setText("Iltimos, o'z jinsingizni tanlang");
                        sendMessage.setReplyMarkup(InlineButtonUtil.genderButtons());
                        telegramBotConfig.sendMsg(sendMessage);
                    } catch (DateTimeException e) {
                        sendMessage.setText("Tug'ilgan kuningizni, to'g'ri kiriting.\nNamuna (24.11.2003)");
                        telegramBotConfig.sendMsg(sendMessage);
                    }
                } else if (entity.getStatus() == HEIGHT) {
                    entity.setHeight(message.getText());
                    entity.setStatus(WEIGHT);

                    questionnaireRepository.save(entity);

                    sendMessage.setText("Vazningizni kiriting. \n Namuna: (65.5-kg)");
                    telegramBotConfig.sendMsg(sendMessage);

                } else if (entity.getStatus() == WEIGHT) {
                    entity.setWeight(message.getText());
                    entity.setStatus(PHONE);

                    questionnaireRepository.save(entity);

                    sendMessage.setText("Telefon raqamingizni kiriting");
                    sendMessage.setReplyMarkup(ButtonUtil.requestContact(UZ));

                    telegramBotConfig.sendMsg(sendMessage);
                } else if (entity.getStatus() == PHONE) {

                    if (message.hasContact())
                        entity.setPhone(message.getContact().getPhoneNumber());
                    else if (message.getText().length() == 13 && message.getText().startsWith("+998") && checkPhoneNumber(message.getText())) {
                        entity.setPhone(message.getText());
                    } else {
                        sendMessage.setText("Telefon raqam xato, iltimos qaytadan kiriting!");
                        sendMessage.setReplyMarkup(ButtonUtil.requestContact(UZ));
                        telegramBotConfig.sendMsg(sendMessage);
                        return;
                    }

                    var remove = new ReplyKeyboardRemove();
                    remove.setRemoveKeyboard(true);

                    sendMessage.setText("Ma'lumotlar qabul qilindi!");
                    sendMessage.setReplyMarkup(remove);
                    telegramBotConfig.sendMsg(sendMessage);

                    if (entity.getGender() == Gender.MALE) {
                        sendMessage.setText(getFormat(entity, "ERKAK"));
                    } else
                        sendMessage.setText(getFormat(entity, "AYOL"));

                    sendMessage.setParseMode("HTML");
                    sendMessage.setReplyMarkup(InlineButtonUtil.formFillFinishButtons(UZ));
                    questionnaireRepository.save(entity);
                    telegramBotConfig.sendMsg(sendMessage);
                }
            }
            case RU -> {
                sendMessage.setText("Rus tilidagi qismi hali yakuniga yetmadi \n\n /start buyrug'ini bosib O'zbek tilida sinab ko'ring! ");
                telegramBotConfig.sendMsg(sendMessage);
            }
        }

    }

    private String getFormat(QuestionnaireEntity entity, String gender) {
        return String.format("""
                        <b>🔎 Iltimos, o'z ma'lumotlaringizni tekshirib chiqing.</b>
                                                    
                        <i>Ism: </i> %s
                        <i>Familya: </i> %s
                        <i>Tug'ilgan sana: </i> %s
                        <i>Jinsingiz: </i> %s
                        <i>Bo'yingiz: </i> %s
                        <i>Vazningiz: </i> %s
                        <i>Telefon raqam: </i> %s
                                                    
                         <b>Agar, o'z ma'lumotlaringizda xatoliklar bo'lsa uni
                         qaytadan to'ldirib chiqing.
                         </b>
                        """,
                entity.getName(), entity.getSurname(),
                entity.getBirthDate().toString(),
                gender,
                entity.getHeight(), entity.getWeight(),
                entity.getPhone());
    }

    public QuestionnaireEntity getByTelegramId(Long telegramId) {
        return questionnaireRepository.findByTelegramId(telegramId).orElse(null);
    }

    public void delete(QuestionnaireEntity entity) {
        questionnaireRepository.delete(entity);
    }

    private boolean checkPhoneNumber(String phone) {
        for (int i = 3; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
