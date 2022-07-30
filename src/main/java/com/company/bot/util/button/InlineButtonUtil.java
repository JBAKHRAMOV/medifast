package com.company.bot.util.button;

import com.company.bot.service.ComplaintsService;
import com.company.bot.enums.Gender;
import com.company.bot.enums.LanguageCode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.company.bot.config.TelegramBotConfig.USER_COMPLAINT;
import static com.company.bot.config.TelegramBotConfig.USER_LIST;
import static com.company.bot.constants.ButtonName.*;
import static com.company.bot.enums.LanguageCode.*;
import static com.company.bot.enums.LanguageCode.UZ;

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
                for (int i = 0; i < ComplaintsService.COMPLAINTS_LIST.size() / 2; i++) {
                    if (i + 1 == ComplaintsService.COMPLAINTS_LIST.size()) {
                        var button = new InlineKeyboardButton();
                        button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                        list.add(row(button));
                    } else {
                        var button = new InlineKeyboardButton();
                        var button2 = new InlineKeyboardButton();
                        button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                        button2 = button(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i + 1).getKey());
                        list.add(row(button, button2));
                        i++;
                    }
                }
                list.add(row(button(NEXT_UZ, NEXT_UZ)));
            }
            case RU -> {
                for (int i = 0; i < ComplaintsService.COMPLAINTS_LIST.size() / 2; i++) {
                    if (i + 1 == ComplaintsService.COMPLAINTS_LIST.size()) {
                        var button = new InlineKeyboardButton();
                        button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                        list.add(row(button));
                    } else {
                        var button = new InlineKeyboardButton();
                        var button2 = new InlineKeyboardButton();
                        button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                        button2 = button(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i + 1).getKey());
                        list.add(row(button, button2));
                        i++;
                    }
                }
                list.add(row(button(NEXT_RU, NEXT_RU)));
            }
        }
        return keyboard(list);
    }

    public static InlineKeyboardMarkup confirmComplints(LanguageCode languageCode) {
        var confirm = new InlineKeyboardButton();
        var stop = new InlineKeyboardButton();
        switch (languageCode) {
            case UZ -> {
                confirm = button(CONFIRM_UZ, CONFIRM_UZ);
                stop = button(AGAIN_UZ, AGAIN_UZ);
            }
            case RU -> {
                confirm = button(CONFIRM_RU, CONFIRM_RU);
                stop = button(AGAIN_RU, AGAIN_RU);
            }
        }
        List<InlineKeyboardButton> row1 = row(confirm);
        List<InlineKeyboardButton> row2 = row(stop);
        return keyboard(rowList(row1, row2));
    }

    public static InlineKeyboardMarkup complaintButtonListSendAgain(LanguageCode languageCode, Long chatId, String data) {

        List<List<InlineKeyboardButton>> list = new LinkedList<>();
        var userComplientList = USER_COMPLAINT.get(chatId);
        var user = USER_LIST.get(chatId);

        switch (languageCode) {
            case UZ -> {

                for (int i = user.getStartLength(); i < ComplaintsService.COMPLAINTS_LIST.size() / user.getFinishLength(); i++) {
                    if (ComplaintsService.COMPLAINTS_LIST.size() == i + 1) {
                        var button = new InlineKeyboardButton();
                        if (!userComplientList.isEmpty()) {
                            for (var userComplaints : userComplientList) {
                                if (userComplaints.getNameUz().equals(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz())) {
                                    button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz() + " ✅", ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                                    break;
                                }
                                else
                                    button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                            }
                        } else
                            button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                        list.add(row(button));
                    } else {
                        var button = new InlineKeyboardButton();
                        var button2 = new InlineKeyboardButton();
                        if (!userComplientList.isEmpty()) {
                            for (var userComplaint : userComplientList) {
                                if (userComplaint.getNameUz().equals(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz())) {
                                    button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz() + " ✅", ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                                    break;
                                }
                                else
                                    button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());

                            }
                        } else
                            button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                        if (!userComplientList.isEmpty()) {
                            for (var userComplaints : userComplientList) {
                                if (userComplaints.getNameUz().equals(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameUz())) {
                                    button2 = button(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameUz() + " ✅", ComplaintsService.COMPLAINTS_LIST.get(i + 1).getKey());
                                    break;
                                }
                                else
                                    button2 = button(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i + 1).getKey());

                            }

                        } else
                            button2 = button(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameUz(), ComplaintsService.COMPLAINTS_LIST.get(i + 1).getKey());
                        i++;
                        list.add(row(button, button2));
                    }
                }
                if (user.getStartLength() == 0) {
                    list.add(row(button(NEXT_UZ, NEXT_UZ)));
                } else {
                    list.add(row(button(BACK_UZ, BACK_UZ), button(STOP_UZ, STOP_UZ)));
                }
            }
            case RU -> {
                for (int i = user.getStartLength(); i < ComplaintsService.COMPLAINTS_LIST.size() / user.getFinishLength(); i++) {
                    if (ComplaintsService.COMPLAINTS_LIST.size() == i + 1) {
                        var button = new InlineKeyboardButton();
                        if (!userComplientList.isEmpty()) {
                            for (var userComplaints : userComplientList) {
                                if (userComplaints.getNameRu().equals(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu())) {
                                    button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu() + " ✅", ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                                    break;
                                }
                                else
                                    button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                            }
                        } else
                            button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                        list.add(row(button));
                    } else {
                        var button = new InlineKeyboardButton();
                        var button2 = new InlineKeyboardButton();
                        if (!userComplientList.isEmpty()) {
                            for (var userComplaint : userComplientList) {
                                if (userComplaint.getNameRu().equals(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu())) {
                                    button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu() + " ✅", ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                                    break;
                                }
                                else
                                    button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());

                            }
                        } else
                            button = button(ComplaintsService.COMPLAINTS_LIST.get(i).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i).getKey());
                        if (!userComplientList.isEmpty()) {
                            for (var userComplaints : userComplientList) {
                                if (userComplaints.getNameRu().equals(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameRu())) {
                                    button2 = button(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameRu() + " ✅", ComplaintsService.COMPLAINTS_LIST.get(i + 1).getKey());
                                    break;
                                }
                                else
                                    button2 = button(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i + 1).getKey());

                            }

                        } else
                            button2 = button(ComplaintsService.COMPLAINTS_LIST.get(i + 1).getNameRu(), ComplaintsService.COMPLAINTS_LIST.get(i + 1).getKey());
                        i++;
                        list.add(row(button, button2));
                    }
                }
                if (user.getStartLength() == 0) {
                    list.add(row(button(NEXT_RU, NEXT_RU)));
                } else {
                    list.add(row(button(BACK_RU, BACK_RU), button(STOP_RU, STOP_RU)));
                }
            }
        }
        return keyboard(list);
    }

    public static InlineKeyboardMarkup cigarette(LanguageCode languageCode) {
        var button1 = new InlineKeyboardButton();
        var button2 = new InlineKeyboardButton();
        var button3 = new InlineKeyboardButton();
        switch (languageCode) {
            case UZ -> {
                button1 = button(CIGARETTA_NO_UZ, CIGARETTA_NO_UZ);
                button2 = button(CIGARETTA_05_1_UZ, CIGARETTA_05_1_UZ);
                button3 = button(CIGARETTA_1_2_UZ, CIGARETTA_1_2_UZ);
            }
            case RU -> {
                button1 = button(CIGARETTA_NO_RU, CIGARETTA_NO_RU);
                button2 = button(CIGARETTA_05_1_RU, CIGARETTA_05_1_RU);
                button3 = button(CIGARETTA_1_2_RU, CIGARETTA_1_2_RU);
            }
        }
        return keyboard(rowList(row(button1),row( button2),row( button3)));
    }

    public static InlineKeyboardMarkup againDataWrite(LanguageCode languageCode) {
        var button1 = new InlineKeyboardButton();
        switch (languageCode) {
            case UZ -> {
                button1 = button(AGAIN_DATA_UZ, AGAIN_DATA_UZ);
            }
            case RU -> {
                button1 = button(AGAIN_DATA_RU, AGAIN_DATA_RU);
            }
        }
        return keyboard(rowList(row(button1)));
    }

}
