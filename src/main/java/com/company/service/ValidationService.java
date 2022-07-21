package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.constants.ButtonName;
import com.company.enums.Gender;
import com.company.enums.LanguageCode;
import com.company.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.DateTimeException;
import java.time.LocalDate;

import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.constants.ButtonName.*;
import static com.company.enums.Gender.FEMALE;
import static com.company.enums.Gender.MALE;
import static com.company.enums.LanguageCode.UZ;

@Component
@RequiredArgsConstructor
public class ValidationService {

    @Lazy
    private final TelegramBotConfig telegramBotConfig;

    public boolean active(Update update) {
        System.out.println("active");
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                var text = update.getMessage().getText();
                if (text.equals(COMPLAINT_RU)
                        || text.equals(COMPLAINT_UZ)
                        || text.equals(BACK_RU)
                        || text.equals(BACK_UZ)
                        || text.equals(FILL_FORM_BTN_UZ)
                        || text.equals(FILL_FORM_BTN_RU)
                        || text.equals(MENU_RU)
                        || text.equals(MENU_UZ)
                        || text.equals(CHANGE_LANG_RU)
                        || text.equals(CHANGE_LANG_UZ)) {
                    System.out.println("true");
                    return true;
                }
            }
        }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean fillFormSome(Update update) {
        System.out.println("fillFormSome");
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                System.out.println("true");
                return true;
            }
        }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean fillFormPhone(Update update) {
        System.out.println("fillFormphone");
        if (update.hasMessage()) {
            if (update.getMessage().hasText() || update.getMessage().hasContact()) {
                System.out.println("true");
                return true;
            }
        }

        sendMsg(update.getMessage());
        return false;
    }

    public boolean fillFormGender(Update update) {
        System.out.println("fillFormGender");

        if (update.hasCallbackQuery()) {
            var text = update.getCallbackQuery().getData();

            if (text.equals(FEMALE.name())
                    || text.equals(MALE.name())) {
                System.out.println("true");
                return true;
            }
        }
        if (update.hasMessage()) {
            var messege = update.getMessage();
            if (messege.hasText()) {
                System.out.println("hasmessage");
                if (checkDate(messege))
                    return true;
                else
                    return false;
            }
        }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean fillFormDefault(Update update) {
        System.out.println("fill from default");
        if (update.hasCallbackQuery()) {
            System.out.println("true ");
            return true;
        }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean notActive(Update update) {
        System.out.println("not active");
        if (update.hasCallbackQuery()) {
            System.out.println("true");
            return true;
        }
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                var text = update.getMessage().getText();
                if (text.equals(ABOUT_BOT_BTN_RU) ||
                        text.equals(ABOUT_BOT_BTN_UZ) |
                                text.equals(FILL_FORM_BTN_RU) ||
                        text.equals(FILL_FORM_BTN_UZ)) {
                    System.out.println("true");
                    return true;
                }
            }

        }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean complainFrom(Update update) {
        System.out.println("complainFrom");
        if (update.hasCallbackQuery()) {
            System.out.println("true");
            return true;
        }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean complainFromInfoInfoWrite(Update update) {
        System.out.println("complainFromInfoInfoWrite");
        if (update.hasMessage())
            if (update.getMessage().hasText() || update.getMessage().hasVoice()) {
                System.out.println("true");
                return true;
            }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean complainFromInfoSome(Update update) {
        System.out.println("complainFromInfoSome");
        if (update.hasMessage())
            if (update.getMessage().hasText()) {
                System.out.println("true");
                return true;
            }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean complainFromInfoDrugsList(Update update) {
        System.out.println("complainFromInfoDrugsList");
        if (update.hasMessage())
            if (update.getMessage().hasText() || update.getMessage().hasPhoto()) {
                System.out.println("true");
                return true;
            }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean complainFromInfoCigareta(Update update) {
        System.out.println("complainFromInfoCigareta");
        if (update.hasCallbackQuery()) {
            System.out.println("true");
            return true;
        }
        sendMsg(update.getMessage());
        return false;
    }
    public boolean changeLang(Update update) {
        System.out.println("changeLAng");
        if (update.hasCallbackQuery()) {
            System.out.println("true");
            return true;
        }
        sendMsg(update.getMessage());
        return false;
    }

    public boolean complainFromInfoinpection(Update update) {
        System.out.println("complainFromInfoinpection");
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                var text = update.getMessage().getText();
                if (text.equals(SKIP_RU)
                        || text.equals(SKIP_UZ)
                        || text.equals(STOP_UZ)
                        || text.equals(STOP_RU)) {
                    System.out.println("true");
                    return true;
                }
            }

            if (update.getMessage().hasPhoto()) {
                return true;
            }
        }
        if (update.hasCallbackQuery()) {
            var text = update.getCallbackQuery().getData();
            if (text.equals(CONFIRM_UZ)
                    || text.equals(CONFIRM_RU)
                    || text.equals(AGAIN_UZ)
                    || text.equals(AGAIN_RU))
                return true;
        }
        sendMsg(update.getMessage());
        return false;
    }



    public void sendMsg(Message message) {
        var lang = USER_LIST.get(message.getChatId()).getLanguageCode();
        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (lang.equals(UZ))
            sendMsg.setText("Faqatgina so'ralgan ma'lumot bot tomonidan qabul qilinadi. \n" +
                    "Iltimos qaytadan to'g'ri ma'lumot jo'natishga urinib ko'ring. \uD83D\uDE0A");
        else
            sendMsg.setText("Только запрошенная информация принимается ботом.\n" +
                    "Пожалуйста, попробуйте отправить правильную информацию еще раз. \uD83D\uDE0A");

        telegramBotConfig.sendMsg(sendMsg);
    }

    public boolean checkDate(Message message) {

        try {
            LocalDate localDate = DateUtil.stringToDate(message.getText());
            System.out.println("check date true");
        } catch (DateTimeException e) {
            var lang = USER_LIST.get(message.getChatId()).getLanguageCode();
            var sendMsg = new SendMessage();
            sendMsg.setChatId(String.valueOf(message.getChatId()));
            if (lang.equals(UZ))
                sendMsg.setText("Tug'ilgan kuningizni, to'g'ri kiriting.\nNamuna (24.11.2003)");
            else
                sendMsg.setText("Пожалуйста, введите дату своего рождения правильно.\nОбразец (24.11.2003)");

            telegramBotConfig.sendMsg(sendMsg);
            System.out.println("check date false");
            return false;
        }
        return true;
    }
}
