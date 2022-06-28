package com.company.util.button;

import com.company.enums.ButtonName;
import com.company.enums.Gender;
import com.company.enums.LanguageCode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.company.enums.ButtonName.*;
import static com.company.enums.LanguageCode.*;
import static com.company.enums.LanguageCode.UZ;

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
}
