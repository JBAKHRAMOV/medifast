package com.company.util.button;

import com.company.enums.Gender;
import com.company.enums.LanguageCode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        var langUzbek = button("\uD83C\uDDFA\uD83C\uDDFF O'zbekcha", LanguageCode.UZ.name());
        var langRussian = button("\uD83C\uDDF7\uD83C\uDDFA Русский", LanguageCode.RU.name());
        List<InlineKeyboardButton> row1 = row(langUzbek);
        List<InlineKeyboardButton> row2 = row(langRussian);
        return keyboard(rowList(row1, row2));
    }

    public static InlineKeyboardMarkup formFillFinishButtons(LanguageCode languageCode) {
        var confirm = new InlineKeyboardButton();
        var again = new InlineKeyboardButton();
        switch (languageCode) {
            case UZ -> {
                confirm = button("✅ Tasdiqlash", "confirm");
                again = button("♻️Qaytadan", "again");
            }
            case RU -> {
                confirm = button("✅ Подтверждение", "confirm");
                again = button("♻️Ещё раз", "again");
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
}
