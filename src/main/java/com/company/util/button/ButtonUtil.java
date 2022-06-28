package com.company.util.button;

import com.company.enums.LanguageCode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.company.enums.ButtonName.*;

public class ButtonUtil {


    public static ReplyKeyboardMarkup keyboard(LanguageCode languageCode) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        keyboard.add(row);
        switch (languageCode) {
            case UZ -> {
                row.add(FILL_FORM_BTN_UZ);
                row.add(ABOUT_BOT_BTN_UZ);
            }
            case RU -> {
                row.add(FILL_FORM_BTN_RU);
                row.add(ABOUT_BOT_BTN_RU);
            }
        }

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup fillFormButton(LanguageCode languageCode) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        keyboard.add(row);
        switch (languageCode) {
            case UZ -> {
                row.add(FILL_FORM_BTN_UZ);
            }
            case RU -> {
                row.add(FILL_FORM_BTN_RU);
            }
        }

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup requestContact(LanguageCode languageCode) {
        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();

        switch (languageCode) {
            case UZ -> {
                keyboardButton.setText(SEND_CANTACT_UZ);
                keyboardButton.setRequestContact(true);

            }
            case RU -> {
                keyboardButton.setText(SEND_CANTACT_RU);
                keyboardButton.setRequestContact(true);
            }
        }
        row.add(keyboardButton);

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(row));
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }
}
