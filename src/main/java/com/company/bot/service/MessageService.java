package com.company.bot.service;

import com.company.bot.config.TelegramBotConfig;
import com.company.bot.constants.ButtonName;
import com.company.bot.util.button.ButtonUtil;
import com.company.bot.util.button.InlineButtonUtil;
import com.company.bot.dto.BotUsersDTO;
import com.company.bot.enums.Gender;
import com.company.bot.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.DateTimeException;
import java.time.LocalDate;

import static com.company.bot.enums.LanguageCode.RU;
import static com.company.bot.enums.LanguageCode.UZ;
import static com.company.bot.enums.UserQuestionnaireStatus.*;
import static com.company.bot.enums.UserStatus.CHANGE_LANG;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;

    public void phone(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        if (message.hasContact())
            dto.setPhone(message.getContact().getPhoneNumber());
        else if (message.getText().length() == 13 && message.getText().startsWith("+998") && checkPhoneNumber(message.getText())) {
            dto.setPhone(message.getText());
        } else {
            if (dto.getLanguageCode().equals(UZ))
                sendMessage.setText("Iltimos telefon raqamingizni to'g'ri kiriting!");
            else
                sendMessage.setText("Номер телефона неверен, пожалуйста, введите его еще раз!");
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
        var user = TelegramBotConfig.USER_LIST.get(message.getChatId());
        user.setQuestionnaireStatus(DEFAULT);
        TelegramBotConfig.USER_LIST.put(user.getTelegramId(), user);

        sendMessage.setParseMode("HTML");
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
        telegramBotConfig.sendMsg(sendMessage);
    }

    public void weight(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        dto.setWeight(message.getText());
        dto.setQuestionnaireStatus(REGION);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

        if (dto.getLanguageCode().equals(UZ)) {
            sendMessage.setText("Iltimos, yashash hududingizni kiriting");
        } else {
            sendMessage.setText("Пожалуйста, укажите свой район проживания");
        }

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void region(Message message, BotUsersDTO dto, SendMessage sendMessage) {
        dto.setRegion(message.getText());
        dto.setQuestionnaireStatus(BLOOD_PRESSURE);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
        if (dto.getLanguageCode().equals(UZ)) {
            sendMessage.setText("Qon bosimingizni kiriting\nYoki o'tkazib yuboring");
            sendMessage.setReplyMarkup(ButtonUtil.skip(UZ));
        } else {
            sendMessage.setText("Введите свое кровяное давление\nИли пропустить");
            sendMessage.setReplyMarkup(ButtonUtil.skip(RU));
        }

        telegramBotConfig.sendMsg(sendMessage);

    }

    public void bloodPressure(Message message, BotUsersDTO dto, SendMessage sendMessage, String text) {

        dto.setBloodPrassure(text);
        dto.setQuestionnaireStatus(HEART_BEAT);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

        if (dto.getLanguageCode().equals(UZ)) {
            sendMessage.setReplyMarkup(ButtonUtil.skip(UZ));
            sendMessage.setText("Yurak urish sonini kiriting\nYoki o'tkazib yuboring");
        } else {
            sendMessage.setText("Введите сердцебиение\nИли пропустить");
            sendMessage.setReplyMarkup(ButtonUtil.skip(RU));
        }

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void heartBeats(Message message, BotUsersDTO dto, SendMessage sendMessage, String text) {
        dto.setHeartBeat(text);
        dto.setQuestionnaireStatus(DIABETES);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

        if (dto.getLanguageCode().equals(UZ)) {
            sendMessage.setReplyMarkup(ButtonUtil.skip(UZ));
            sendMessage.setText("Qandli diabetingiz bo'lsa kirting\nYoki o'tkazib yuboring");
        } else {
            sendMessage.setText("Введите, если у вас диабет\nИли пропустить");
            sendMessage.setReplyMarkup(ButtonUtil.skip(RU));
        }

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void diabeats(Message message, BotUsersDTO dto, SendMessage sendMessage, String text) {
        dto.setDiabets(text);
        dto.setQuestionnaireStatus(TEMPERATURE);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);

        if (dto.getLanguageCode().equals(UZ)) {
            sendMessage.setReplyMarkup(ButtonUtil.skip(UZ));
            sendMessage.setText("Temperaturangizni kiriting\nYoki o'tkazib yuboring");
        } else {
            sendMessage.setText("Введите температуру\nИли пропустить");
            sendMessage.setReplyMarkup(ButtonUtil.skip(RU));
        }

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void tempratura(Message message, BotUsersDTO dto, SendMessage sendMessage, String text) {
        dto.setTemprature(text);
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
            sendMessage.setText("Пожалуйста, введите Ваше фамилию.");

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

        dto.setQuestionnaireStatus(SURNAME);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), dto);
    }


    public void complaintsInfoWrite(Message message, BotUsersDTO user) {
        var infoDTO = TelegramBotConfig.USER_COMPLAINT_INFO.get(message.getChatId());
        infoDTO.setCauseOfComplaint(message.getText());
        TelegramBotConfig.USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);
        user.setQuestionnaireStatus(COMPLAINTS_STARTED_TIME);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), user);
        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Shikoyatlar qachon boshlandi?");
        else
            sendMsg.setText("Когда начались жалобы?");
        telegramBotConfig.sendMsg(sendMsg);

    }

    public void complaintsStartedDate(Message message, BotUsersDTO user) {
        var infoDTO = TelegramBotConfig.USER_COMPLAINT_INFO.get(message.getChatId());
        infoDTO.setComplaintStartedTime(message.getText());
        TelegramBotConfig.USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);
        user.setQuestionnaireStatus(DRUGS_LIST);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), user);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Qabul qilgan va qilayotgan dorilaringizni yozib jo'nating yoki rasmga olib jonating: ");
        else
            sendMsg.setText("Запишите или отправьте фотографии лекарств, которые вы принимаете и принимаете: ");
        telegramBotConfig.sendMsg(sendMsg);

    }

    public void drugsList(Message message, BotUsersDTO user) {
        var infoDTO = TelegramBotConfig.USER_COMPLAINT_INFO.get(message.getChatId());
        if (!message.getText().equals(ButtonName.STOP_UZ) && !message.getText().equals(ButtonName.STOP_RU)) {
            infoDTO.setDrugsList(message.getText());
            TelegramBotConfig.USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);
        }
        user.setQuestionnaireStatus(CIGARETTE);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), user);


        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        var remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        sendMsg.setReplyMarkup(remove);
        sendMsg.setText("...");
        int id = 0;
        try {
            id = telegramBotConfig.execute(sendMsg).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        var delete = new DeleteMessage();
        delete.setMessageId(id);
        delete.setChatId(String.valueOf(message.getChatId()));
        telegramBotConfig.sendMsg(delete);

        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Sigaret chekasizmi?");
        else
            sendMsg.setText("Ты куришь?");
        sendMsg.setReplyMarkup(InlineButtonUtil.cigarette(user.getLanguageCode()));
        telegramBotConfig.sendMsg(sendMsg);

    }

    public void disiasesList(Message message, BotUsersDTO user) {
        var infoDTO = TelegramBotConfig.USER_COMPLAINT_INFO.get(message.getChatId());
        infoDTO.setDiseasesList(message.getText());
        TelegramBotConfig.USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);
        user.setQuestionnaireStatus(INSPECTION_PAPERS);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), user);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("O’tkazilgan tekshiruv qog’ozlari bo’lsa rasmini yoki rasmga tushirib yuboring (ohirgi 2 oydagisi)" +
                    "\nRasm jo'natib bo'lganingizdan so'ng tugatish tugmasini bosing ");
        else
            sendMsg.setText("Если у вас есть документы о проверке, сфотографируйте или пришлите фото (за последние 2 месяца)" +
                    "\nКогда вы закончите отправку изображения, нажмите кнопку «Готово».");
        sendMsg.setReplyMarkup(ButtonUtil.next(user.getLanguageCode()));
        telegramBotConfig.sendMsg(sendMsg);


    }

    public void changeLanguage(Message message) {
        var user = TelegramBotConfig.USER_LIST.get(message.getChatId());
        user.setStatus(CHANGE_LANG);
        TelegramBotConfig.USER_LIST.put(message.getChatId(), user);
        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        sendMsg.setText("Iltimos, tilni tanlang. / Пожалуйста, выберите язык.");
        sendMsg.setReplyMarkup(InlineButtonUtil.languageButtons());
        telegramBotConfig.sendMsg(sendMsg);
    }

    private String getFormat(BotUsersDTO dto, String gender) {
        var str = String.format("""
                        <b>🔎 Iltimos, o'z ma'lumotlaringizni tekshirib chiqing.</b>
                                                    
                        <i>Ism: </i> %s
                        <i>Familya: </i> %s
                        <i>Tug'ilgan sana: </i> %s
                        <i>Jinsingiz: </i> %s
                        <i>Bo'yingiz: </i> %s
                        <i>Vazningiz: </i> %s
                        <i>Yashash hududingiz: </i> %s
                        """,
                dto.getName(), dto.getSurname(),
                dto.getBirthDate().toString(),
                gender,
                dto.getHeight(), dto.getWeight(),
                dto.getRegion());


        StringBuilder builder = new StringBuilder(str);
        if (dto.getBloodPrassure() != null)
            builder.append(String.format("<i>Qon bosim: </i> %s\n", dto.getBloodPrassure()));
        if (dto.getDiabets() != null)
            builder.append(String.format("<i>Qandli diabet: </i> %s\n", dto.getDiabets()));
        if (dto.getTemprature() != null)
            builder.append(String.format("<i>Tempratura: </i> %s\n", dto.getTemprature()));
        if (dto.getHeartBeat() != null)
            builder.append(String.format("<i>Yurak urishi: </i> %s\n", dto.getHeartBeat()));

        builder.append(String.format("<i>Telefon raqam: </i> %s\n\n", dto.getPhone()));
        builder.append("""
                <b>Agar, o'z ma'lumotlaringizda xatoliklar bo'lsa uni
                qaytadan to'ldirib chiqing.
                </b>""");

        return builder.toString();
    }

    private String getFormatRU(BotUsersDTO dto, String gender) {
        var str = String.format("""
                        <b>🔎 Пожалуйста, проверьте вашу информацию.</b>
                                                    
                        <i>Имя: </i> %s
                        <i>Фамилия: </i> %s
                        <i>Дата рождения: </i> %s
                        <i>твой пол: </i> %s
                        <i>Твой рост: </i> %s
                        <i>Твой вес: </i> %s
                        <i>ваш район: </i> %s
                        """,
                dto.getName(), dto.getSurname(),
                dto.getBirthDate().toString(),
                gender,
                dto.getHeight(), dto.getWeight(),
                dto.getRegion());

        StringBuilder builder = new StringBuilder(str);
        if (dto.getBloodPrassure() != null)
            builder.append(String.format("<i>Кровяное давление: </i> %s\n", dto.getBloodPrassure()));
        if (dto.getDiabets() != null)
            builder.append(String.format("<i>Диабет: </i> %s\n", dto.getDiabets()));
        if (dto.getTemprature() != null)
            builder.append(String.format("<i>Температура: </i> %s\n", dto.getTemprature()));
        if (dto.getHeartBeat() != null)
            builder.append(String.format("<i>Стук сердца: </i> %s\n", dto.getHeartBeat()));

        builder.append(String.format("<i>Номер телефона: </i> %s\n\n", dto.getPhone()));
        builder.append("""
                <b>Если в ваших данных есть ошибки
                Пожалуйста, заполните его снова.
                </b>""");

        return builder.toString();
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
