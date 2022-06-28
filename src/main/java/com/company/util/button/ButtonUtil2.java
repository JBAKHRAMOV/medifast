package com.company.util.button;

import com.company.enums.ButtonName;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.company.enums.ButtonName.MENU_UZ;

public class ButtonUtil2 {

    public static InlineKeyboardButton button(String text, String callBack) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBack);
        return button;
    }

    public static List<InlineKeyboardButton> row(InlineKeyboardButton... buttons) {
        return new LinkedList<>(Arrays.asList(buttons));
    }

    public static List<List<InlineKeyboardButton>> rowList(List<InlineKeyboardButton>... rows) {
        return new LinkedList<>(Arrays.asList(rows));
    }

    public static InlineKeyboardMarkup keyboard(List<List<InlineKeyboardButton>> rows) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    /**
     * Utils Keyboards
     */
    public static InlineKeyboardMarkup menuSingleKeyboard() {
        InlineKeyboardButton button = InlineButtonUtil.button(MENU_UZ, MENU_UZ);
        InlineKeyboardButton button1 = InlineButtonUtil.button("Keyboard Button", "keyboard");
        List<InlineKeyboardButton> row = InlineButtonUtil.row(button);
        List<InlineKeyboardButton> row1= InlineButtonUtil.row(button1);
        return InlineButtonUtil.keyboard(InlineButtonUtil.rowList(row,row1));
    }
}
