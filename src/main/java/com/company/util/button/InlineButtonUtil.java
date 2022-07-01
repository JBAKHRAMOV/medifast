package com.company.util.button;

import com.company.config.TelegramBotConfig;
import com.company.enums.Gender;
import com.company.enums.LanguageCode;
import com.company.service.ComplaintsService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.company.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.constants.ButtonName.*;
import static com.company.enums.LanguageCode.*;
import static com.company.enums.LanguageCode.UZ;
import static com.company.service.ComplaintsService.COMPLAINTS_LIST;

public class InlineButtonUtil {
    public static InlineKeyboardButton button(String text, String callBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBackData);
        return button;
    }


    public static List<InlineKeyboardButton> row(InlineKeyboardButton... inlineKeyboardButtons) {
        return new LinkedList<>(Arrays.asList(inlineKeyboardButtons));
    }

    @SafeVarargs
    public static List<List<InlineKeyboardButton>> rowList(List<InlineKeyboardButton>... rows) {
        return new LinkedList<>(Arrays.asList(rows));
    }

    public static InlineKeyboardMarkup keyboard(List<List<InlineKeyboardButton>> rowList) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rowList);
        return keyboardMarkup;
    }


    /**
     * Buttons
     **/


    public static InlineKeyboardMarkup sendSmsButton() {
        InlineKeyboardButton sendSms = button("✅ Xabarni jo'natish", "sendSms");
        InlineKeyboardButton again = button("♻️Qaytadan", "again");
        InlineKeyboardButton cancel = button("❌ Bekor qilish", "cancel");
        List<InlineKeyboardButton> row1 = row(sendSms);
        List<InlineKeyboardButton> row2 = row(again, cancel);

        return keyboard(rowList(row1, row2));
    }

    public static InlineKeyboardMarkup languageButtons() {
        var langUzbek = button(LANGUAGE_UZ, UZ.name());
        var langRussian = button(LANGUAGE_RU, RU.name());
        List<InlineKeyboardButton> row1 = row(langUzbek);
        List<InlineKeyboardButton> row2 = row(langRussian);
        return keyboard(rowList(row1, row2));
    }

    public static InlineKeyboardMarkup formFillFinishButtons(LanguageCode languageCode) {
        var confirm = new InlineKeyboardButton();
        var again = new InlineKeyboardButton();
        switch (languageCode) {
            case UZ -> {
                confirm = button(CONFIRM_UZ, CONFIRM_UZ);
                again = button(AGAIN_UZ, AGAIN_UZ);
            }
            case RU -> {
                confirm = button(CONFIRM_RU, CONFIRM_RU);
                again = button(AGAIN_RU, AGAIN_RU);
            }
        }
        List<InlineKeyboardButton> row1 = row(confirm);
        List<InlineKeyboardButton> row2 = row(again);
        return keyboard(rowList(row1, row2));
    }

    public static InlineKeyboardMarkup genderButtons() {
        InlineKeyboardButton female = button("👩‍", Gender.FEMALE.name());
        InlineKeyboardButton male = button("🧔‍♂️", Gender.MALE.name());
        List<InlineKeyboardButton> row1 = row(female, male);
        return keyboard(rowList(row1));
    }

    public static InlineKeyboardMarkup complaintButtonList(LanguageCode languageCode) {

        List<List<InlineKeyboardButton>> list = new LinkedList<>();

        switch (languageCode) {
            case UZ -> {
                for (int i = 0; i < COMPLAINTS_LIST.size(); i++) {
                    if (i + 1 == COMPLAINTS_LIST.size()) {
                        var button = new InlineKeyboardButton();
                        button = button(COMPLAINTS_LIST.get(i).getNameUz(), COMPLAINTS_LIST.get(i).getKey());
                        list.add(row(button));
                    } else {
                        var button = new InlineKeyboardButton();
                        var button2 = new InlineKeyboardButton();
                        button = button(COMPLAINTS_LIST.get(i).getNameUz(), COMPLAINTS_LIST.get(i).getKey());
                        button2 = button(COMPLAINTS_LIST.get(i + 1).getNameUz(), COMPLAINTS_LIST.get(i + 1).getKey());
                        list.add(row(button, button2));
                    }
                }
                list.add(row(button(STOP_UZ, STOP_UZ)));
            }
            case RU -> {
                for (var complints : COMPLAINTS_LIST) {
                    var button = new InlineKeyboardButton();
                    button = button(complints.getNameRu(), complints.getKey());
                    list.add(row(button));
                }
                list.add(row(button(STOP_RU, STOP_RU)));
            }
        }
        return keyboard(list);
    }

    public static InlineKeyboardMarkup confirmComplints(LanguageCode languageCode) {
        var confirm = new InlineKeyboardButton();
        var stop = new InlineKeyboardButton();
        switch (languageCode) {
            case UZ -> {
                confirm = button(MESSSSAGE_SEND_UZ, MESSSSAGE_SEND_UZ);
                stop = button(MESSSSAGE_SEND_STOP_UZ, MESSSSAGE_SEND_RU);
            }
            case RU -> {
                confirm = button(MESSSSAGE_SEND_RU, MESSSSAGE_SEND_RU);
                stop = button(MESSSSAGE_SEND_STOP_RU, MESSSSAGE_SEND_STOP_RU);
            }
        }
        List<InlineKeyboardButton> row1 = row(confirm);
        List<InlineKeyboardButton> row2 = row(stop);
        return keyboard(rowList(row1, row2));
    }

    public static InlineKeyboardMarkup complaintButtonListSendAgain(LanguageCode languageCode, Long chatId) {

        List<List<InlineKeyboardButton>> list = new LinkedList<>();
        var userComplientList = USER_COMPLAINT.get(chatId);

        switch (languageCode) {
            case UZ -> {
                for (var complints : COMPLAINTS_LIST) {
                    var button = new InlineKeyboardButton();
                    for (var userComplaints : userComplientList) {
                        if (userComplaints.getNameUz().equals(complints.getNameUz())) {
                            button = button(complints.getNameUz() + " ✅", complints.getKey());
                            break;
                        } else
                            button = button(complints.getNameUz(), complints.getKey());
                    }
                    list.add(row(button));
                }
            }
            case RU -> {
                for (var complints : COMPLAINTS_LIST) {
                    var button = new InlineKeyboardButton();
                    button = button(complints.getNameRu(), complints.getKey());
                    list.add(row(button));
                }
            }
        }
        return keyboard(list);
    }
}
