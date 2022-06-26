package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
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

    public void create(Message message) {

        var user = message.getFrom();
        var dto = TelegramBotConfig.USER_LIST.get(message.getChatId());

        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));

        switch (dto.getLanguageCode()) {
            case UZ -> {
                if (dto.getQuestionnaireStatus() == NAME) {
                    var remove = new ReplyKeyboardRemove();
                    remove.setRemoveKeyboard(true);

                    sendMessage.setText("Iltimos, ismingizni kiriting.");
                    sendMessage.setReplyMarkup(remove);
                    telegramBotConfig.sendMsg(sendMessage);
                    System.out.println(dto);

                    dto.setQuestionnaireStatus(SURNAME);
                    System.out.println(dto);
                    TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

                } else if (dto.getQuestionnaireStatus() == SURNAME) {
                    sendMessage.setText("Iltimos, familyangizni kiriting.");

                    telegramBotConfig.sendMsg(sendMessage);

                    dto.setName(message.getText());
                    dto.setQuestionnaireStatus(BIRTH_DATE);
                    TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

                } else if (dto.getQuestionnaireStatus() == BIRTH_DATE) {
                    sendMessage.setText("Iltimos, tug'ilgan kuningizni kiriting. \nNamuna (24.11.2003)");
                    telegramBotConfig.sendMsg(sendMessage);

                    dto.setSurname(message.getText());
                    dto.setQuestionnaireStatus(GENDER);
                    TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

                } else if (dto.getQuestionnaireStatus() == GENDER) {
                    try {
                        LocalDate localDate = DateUtil.stringToDate(message.getText());

                        dto.setBirthDate(localDate);
                        dto.setQuestionnaireStatus(GENDER);
                        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

                        sendMessage.setText("Iltimos, o'z jinsingizni tanlang");
                        sendMessage.setReplyMarkup(InlineButtonUtil.genderButtons());
                        telegramBotConfig.sendMsg(sendMessage);
                    } catch (DateTimeException e) {
                        sendMessage.setText("Tug'ilgan kuningizni, to'g'ri kiriting.\nNamuna (24.11.2003)");
                        telegramBotConfig.sendMsg(sendMessage);
                    }
                } else if (dto.getQuestionnaireStatus() == HEIGHT) {
                    dto.setHeight(message.getText());
                    dto.setQuestionnaireStatus(WEIGHT);

                    TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

                    sendMessage.setText("Vazningizni kiriting. \n Namuna: (65.5-kg)");
                    telegramBotConfig.sendMsg(sendMessage);

                } else if (dto.getQuestionnaireStatus() == WEIGHT) {
                    dto.setWeight(message.getText());
                    dto.setQuestionnaireStatus(PHONE);
                    TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

                    sendMessage.setText("Telefon raqamingizni kiriting");
                    sendMessage.setReplyMarkup(ButtonUtil.requestContact(UZ));

                    telegramBotConfig.sendMsg(sendMessage);
                } else if (dto.getQuestionnaireStatus() == PHONE) {

                    if (message.hasContact())
                        dto.setPhone(message.getContact().getPhoneNumber());
                    else if (message.getText().length() == 13 && message.getText().startsWith("+998") && checkPhoneNumber(message.getText())) {
                        dto.setPhone(message.getText());
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

                    if (dto.getGender() == Gender.MALE) {
                        sendMessage.setText(getFormat(dto, "ERKAK"));
                    } else
                        sendMessage.setText(getFormat(dto, "AYOL"));

                    sendMessage.setParseMode("HTML");
                    sendMessage.setReplyMarkup(InlineButtonUtil.formFillFinishButtons(UZ));
                    TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
                    telegramBotConfig.sendMsg(sendMessage);
                }
            }
            case RU -> {
                sendMessage.setText("Rus tilidagi qismi hali yakuniga yetmadi \n\n /start buyrug'ini bosib O'zbek tilida sinab ko'ring! ");
                telegramBotConfig.sendMsg(sendMessage);
            }
        }

    }

    private String getFormat(BotUsersDTO dto, String gender) {
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
                dto.getName(), dto.getSurname(),
                dto.getBirthDate().toString(),
                gender,
                dto.getHeight(), dto.getWeight(),
                dto.getPhone());
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
