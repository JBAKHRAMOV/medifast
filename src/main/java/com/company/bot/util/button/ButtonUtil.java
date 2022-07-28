package com.company.bot.util.button;

import com.company.bot.enums.LanguageCode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.company.bot.constants.ButtonName.*;

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
            case UZ -> row.add(FILL_FORM_BTN_UZ);
            case RU -> row.add(FILL_FORM_BTN_RU);
            default -> throw new IllegalStateException("Unexpected value: " + languageCode);
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
                keyboardButton.setText(SEND_CONTACT_UZ);
                keyboardButton.setRequestContact(true);

            }
            case RU -> {
                keyboardButton.setText(SEND_CONTACT_RU);
                keyboardButton.setRequestContact(true);
            }
        }
        row.add(keyboardButton);

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(row));
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }
    public static ReplyKeyboardMarkup skip(LanguageCode languageCode) {
        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();

        switch (languageCode) {
            case UZ -> {
                keyboardButton.setText(SKIP_UZ);

            }
            case RU -> {
                keyboardButton.setText(SKIP_RU);
            }
        }
        row.add(keyboardButton);

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(row));
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup complaintsMenu(LanguageCode languageCode) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        keyboard.add(row);
        keyboard.add(row2);
        switch (languageCode) {
            case UZ -> {
                row.add(COMPLAINT_UZ);
                row.add(BACK_UZ);
                row2.add(CHANGE_LANG_UZ);
            }
            case RU -> {
                row.add(COMPLAINT_RU);
                row.add(BACK_RU);
                row2.add(CHANGE_LANG_RU);
            }
        }

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup comlaintsStop(LanguageCode languageCode) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        keyboard.add(row);
        switch (languageCode) {
            case UZ -> row.add(STOP_UZ);
            case RU -> row.add(STOP_RU);
            default -> throw new IllegalStateException("Unexpected value: " + languageCode);
        }

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
    public static ReplyKeyboardMarkup adminMainMenu(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        keyboard.add(row);

        row.add(ADMIN_STATS);
        row.add(BROADCAST_A_MESSAGE);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
    public static ReplyKeyboardMarkup adminBroadcastMsgButton(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        keyboard.add(row);

        row.add(SEND);
        row.add(AGAIN_UZ);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
    public static ReplyKeyboardMarkup next(LanguageCode languageCode) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        keyboard.add(row);
        switch (languageCode) {
            case UZ -> {
                row.add(SKIP_UZ);
            }
            case RU -> {
                row.add(SKIP_RU);
            }
        }
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
    public static ReplyKeyboardMarkup photoNext(LanguageCode languageCode){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        keyboard.add(row);
        switch (languageCode) {
            case UZ -> {
                row.add(STOP_UZ);
            }
            case RU -> {
                row.add(STOP_RU);
            }
        }
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup backButtonClick(LanguageCode languageCode){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        keyboard.add(row);
        switch (languageCode) {
            case UZ -> {
                row.add(FILL_FORM_BTN_UZ);
                row.add(MENU_UZ);
                row2.add(ABOUT_BOT_BTN_UZ);
            }
            case RU -> {
                row.add(FILL_FORM_BTN_RU);
                row.add(MENU_RU);
                row2.add(ABOUT_BOT_BTN_RU);
            }
        }
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup menu(LanguageCode languageCode){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        keyboard.add(row);
        switch (languageCode) {
            case UZ -> {
                row.add(MENU_UZ);
            }
            case RU -> {
                row.add(MENU_RU);
            }
        }
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

}
