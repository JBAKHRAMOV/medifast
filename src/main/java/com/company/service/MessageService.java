package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.enums.Gender;
import com.company.enums.UserQuestionnaireStatus;
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

import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.enums.LanguageCode.UZ;
import static com.company.enums.UserQuestionnaireStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;

    public void rus(SendMessage sendMessage) {
        sendMessage.setText("Rus tilidagi qismi hali yakuniga yetmadi \n\n /start buyrug'ini bosib O'zbek tilida sinab ko'ring! ");
        telegramBotConfig.sendMsg(sendMessage);
    }

    public void phone(Message message, BotUsersDTO dto, SendMessage sendMessage) {
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

    public void weight(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        dto.setWeight(message.getText());
        dto.setQuestionnaireStatus(PHONE);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

        sendMessage.setText("Telefon raqamingizni kiriting");
        sendMessage.setReplyMarkup(ButtonUtil.requestContact(UZ));

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void height(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        dto.setHeight(message.getText());
        dto.setQuestionnaireStatus(WEIGHT);

        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

        sendMessage.setText("Vazningizni kiriting. \n Namuna: (65.5-kg)");
        telegramBotConfig.sendMsg(sendMessage);
    }

    public void gender(Message message, BotUsersDTO dto, SendMessage sendMessage) {
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
    }

    public void birthDate(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        sendMessage.setText("Iltimos, tug'ilgan kuningizni kiriting. \nNamuna (24.11.2003)");
        telegramBotConfig.sendMsg(sendMessage);

        dto.setSurname(message.getText());
        dto.setQuestionnaireStatus(GENDER);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
    }

    public void surname(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        sendMessage.setText("Iltimos, familyangizni kiriting.");

        telegramBotConfig.sendMsg(sendMessage);

        dto.setName(message.getText());
        dto.setQuestionnaireStatus(BIRTH_DATE);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
    }

    public void name(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        var remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);

        sendMessage.setText("Iltimos, ismingizni kiriting.");
        sendMessage.setReplyMarkup(remove);
        telegramBotConfig.sendMsg(sendMessage);
        System.out.println(dto);

        dto.setQuestionnaireStatus(SURNAME);
        System.out.println(dto);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
    }

    public static void defaults(Message message, BotUsersDTO botUser) {
        botUser.setQuestionnaireStatus(UserQuestionnaireStatus.NAME);
        USER_LIST.put(message.getChatId(), botUser);
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

    private boolean checkPhoneNumber(String phone) {
        for (int i = 3; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
