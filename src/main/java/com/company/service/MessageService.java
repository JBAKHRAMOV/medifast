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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.time.DateTimeException;
import java.time.LocalDate;

import static com.company.config.TelegramBotConfig.*;
import static com.company.enums.LanguageCode.RU;
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
            if (dto.getLanguageCode().equals(UZ))
                sendMessage.setText("Номер телефона неверен, пожалуйста, введите его еще раз!");
            else
                sendMessage.setText("");
            sendMessage.setReplyMarkup(ButtonUtil.requestContact(UZ));
            telegramBotConfig.sendMsg(sendMessage);
            return;
        }

        var remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);

        if (dto.getLanguageCode().equals(UZ))
            sendMessage.setText("Ma'lumotlar qabul qilindi!");
        else
            sendMessage.setText("Информация получена!");
        sendMessage.setReplyMarkup(remove);

        telegramBotConfig.sendMsg(sendMessage);

        if (dto.getGender() == Gender.MALE) {
            if (dto.getLanguageCode().equals(UZ)) {
                sendMessage.setText(getFormat(dto, "ERKAK"));
                sendMessage.setReplyMarkup(InlineButtonUtil.formFillFinishButtons(UZ));
            } else {
                sendMessage.setReplyMarkup(InlineButtonUtil.formFillFinishButtons(RU));
                sendMessage.setText(getFormatRU(dto, "Мужской"));
            }

        } else {
            if (dto.getLanguageCode().equals(UZ)) {
                sendMessage.setReplyMarkup(InlineButtonUtil.formFillFinishButtons(UZ));
                sendMessage.setText(getFormat(dto, "AYOL"));
            } else {

                sendMessage.setReplyMarkup(InlineButtonUtil.formFillFinishButtons(RU));
                sendMessage.setText(getFormatRU(dto, "Женщина"));
            }
        }

        sendMessage.setParseMode("HTML");
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
        telegramBotConfig.sendMsg(sendMessage);
    }

    public void weight(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        dto.setWeight(message.getText());
        dto.setQuestionnaireStatus(PHONE);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

        if (dto.getLanguageCode().equals(UZ)) {
            sendMessage.setReplyMarkup(ButtonUtil.requestContact(UZ));
            sendMessage.setText("Telefon raqamingizni kiriting");
        } else {
            sendMessage.setText("Введите свой номер телефона");
            sendMessage.setReplyMarkup(ButtonUtil.requestContact(RU));
        }

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void height(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        dto.setHeight(message.getText());
        dto.setQuestionnaireStatus(WEIGHT);

        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

        if (dto.getLanguageCode().equals(UZ))
            sendMessage.setText("Vazningizni kiriting. \nNamuna: (65.5-kg)");
        else
            sendMessage.setText("Введите свой вес.\nОбразец: (65,5 кг)");

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void gender(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        try {
            LocalDate localDate = DateUtil.stringToDate(message.getText());

            dto.setBirthDate(localDate);
            dto.setQuestionnaireStatus(GENDER);
            TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

            if (dto.getLanguageCode().equals(UZ))
                sendMessage.setText("Iltimos, o'z jinsingizni tanlang");
            else
                sendMessage.setText("Пожалуйста, выберите Ваш пол");
            sendMessage.setReplyMarkup(InlineButtonUtil.genderButtons());

            telegramBotConfig.sendMsg(sendMessage);
        } catch (DateTimeException e) {

            if (dto.getLanguageCode().equals(UZ))
                sendMessage.setText("Tug'ilgan kuningizni, to'g'ri kiriting.\nNamuna (24.11.2003)");
            else
                sendMessage.setText("Пожалуйста, введите дату своего рождения правильно.\nОбразец (24.11.2003)");
            telegramBotConfig.sendMsg(sendMessage);
        }
    }

    public void birthDate(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        if (dto.getLanguageCode().equals(UZ))
            sendMessage.setText("Iltimos, tug'ilgan kuningizni kiriting. \nNamuna (24.11.2003)");
        else
            sendMessage.setText("Пожалуйста, введите свой день рождения.\nОбразец (24.11.2003)");

        telegramBotConfig.sendMsg(sendMessage);

        dto.setSurname(message.getText());
        dto.setQuestionnaireStatus(GENDER);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
    }

    public void surname(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        if (dto.getLanguageCode().equals(UZ))
            sendMessage.setText("Iltimos, familyangizni kiriting.");
        else
            sendMessage.setText("Пожалуйста, введите свою фамилию.");

        telegramBotConfig.sendMsg(sendMessage);

        dto.setName(message.getText());
        dto.setQuestionnaireStatus(BIRTH_DATE);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
    }

    public void name(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        var remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);

        if (dto.getLanguageCode().equals(UZ))
            sendMessage.setText("Iltimos, ismingizni kiriting.");
        else
            sendMessage.setText("Пожалуйста, введите Ваше имя.");
        sendMessage.setReplyMarkup(remove);

        telegramBotConfig.sendMsg(sendMessage);
        System.out.println(dto);

        dto.setQuestionnaireStatus(SURNAME);
        System.out.println(dto);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
    }


    public void complaintsInfoWrite(Message message, BotUsersDTO user) {
        var infoDTO = USER_COMPLAINT_INFO.get(message.getChatId());
        infoDTO.setCauseOfComplaint(message.getText());
        USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);
        user.setQuestionnaireStatus(COMPLAINTS_STARTED_TIME);
        USER_LIST.put(message.getChatId(), user);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Shikoyatlar qachon boshlandi?");
        else
            sendMsg.setText("Когда начались жалобы?");
        telegramBotConfig.sendMsg(sendMsg);

    }

    public void complaintsStartedDate(Message message, BotUsersDTO user) {
        var infoDTO = USER_COMPLAINT_INFO.get(message.getChatId());
        infoDTO.setComplaintStartedTime(message.getText());
        USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);
        user.setQuestionnaireStatus(DRUGS_LIST);
        USER_LIST.put(message.getChatId(), user);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Qabul qilgan va qilayotgan dorilaringizni yozib jo'nating yoki rasmga olib jonating: ");
        else
            sendMsg.setText("Запишите или отправьте фотографии лекарств, которые вы принимаете и принимаете: ");
        telegramBotConfig.sendMsg(sendMsg);

    }

    public void drugsList(Message message, BotUsersDTO user) {
        var infoDTO = USER_COMPLAINT_INFO.get(message.getChatId());
        infoDTO.setDrugsList(message.getText());
        USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);
        user.setQuestionnaireStatus(CIGARETTE);
        USER_LIST.put(message.getChatId(), user);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Sigaret chekasizmi?");
        else
            sendMsg.setText("Ты куришь?");
        sendMsg.setReplyMarkup(InlineButtonUtil.cigarette(user.getLanguageCode()));
        telegramBotConfig.sendMsg(sendMsg);

    }

    public void disiasesList(Message message, BotUsersDTO user) {
        var infoDTO = USER_COMPLAINT_INFO.get(message.getChatId());
        infoDTO.setDiseasesList(message.getText());
        USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);
        user.setQuestionnaireStatus(INSPECTION_PAPERS);
        USER_LIST.put(message.getChatId(), user);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("O’tkazilgan tekshiruv qog’ozlari bo’lsa rasmini yoki rasmga tushirib yuboring (ohirgi 2 oydagisi)" +
                    "\nRasm jo'natib bo'lganingizdan so'ng tugatish tugamsini bosing ");
        else
            sendMsg.setText("Если у вас есть документы о проверке, сфотографируйте или пришлите фото (за последние 2 месяца)" +
                    "\nКогда вы закончите отправку изображения, нажмите кнопку «Готово».");
        sendMsg.setReplyMarkup(InlineButtonUtil.next(user.getLanguageCode()));
        telegramBotConfig.sendMsg(sendMsg);


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

    private String getFormatRU(BotUsersDTO dto, String gender) {
        return String.format("""
                        <b>🔎 Пожалуйста, проверьте вашу информацию.</b>
                                                    
                        <i>Имя: </i> %s
                        <i>Фамилия: </i> %s
                        <i>Дата рождения: </i> %s
                        <i>твой пол: </i> %s
                        <i>Твой рост: </i> %s
                        <i>Твой вес: </i> %s
                        <i>Номер телефона: </i> %s
                                                    
                         <b>Если в ваших данных есть ошибки
                           Пожалуйста, заполните его снова.
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
