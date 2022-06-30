package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.constants.ButtonName;
import com.company.dto.admin.AdminDTO;
import com.company.entity.BotUsersEntity;
import com.company.enums.admin.AdminStatus;
import com.company.enums.admin.BroadcastMSGStatus;
import com.company.repository.BotUsersRepository;
import com.company.util.button.ButtonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.company.enums.admin.AdminStatus.*;
import static com.company.enums.admin.BroadcastMSGStatus.*;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final BotUsersRepository botUsersRepository;


    @Value("${user.admin}")
    private Long adminId;

    public void handleStartMessage(Message message) {
        var sendMessage = new SendMessage();
        var adminDTO = AdminDTO.getInstance();
        adminDTO.setBroadcastMSGStatus(STARTED);

        sendMessage.setChatId(String.valueOf(adminId));

        sendMessage.setText("Assalomu alaykum, " + message.getFrom().getFirstName() + "" + "\nBoshqaruv bo'limiga xush kelibsiz 😊");
        sendMessage.setReplyMarkup(ButtonUtil.adminMainMenu());

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void broadcastAMessage(Message message) {

        var sendMessage = new SendMessage();
        var remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);

        var adminDTO = AdminDTO.getInstance();
        adminDTO.setStatus(BROADCAST_A_MSG);

        sendMessage.setChatId(String.valueOf(adminId));

        switch (adminDTO.getBroadcastMSGStatus()) {
            case STARTED -> {
                sendMessage.setText("""
                        Barcha foydalanuvchilarga xabar jo'natish uchun, rasm yoki tekst jo'nating
                        """);
                sendMessage.setReplyMarkup(remove);

                telegramBotConfig.sendMsg(sendMessage);
                adminDTO.setBroadcastMSGStatus(INSPECTION);
            }
            case INSPECTION -> {
                if (message.hasPhoto()) {
                    SendPhoto msg = getSendPhoto(message);

                    telegramBotConfig.sendMsg(msg);
                } else if (message.hasText()) {
                    sendMessage.setText(message.getText());
                    sendMessage.setReplyMarkup(ButtonUtil.adminBroadcastMsgButton());
                    telegramBotConfig.sendMsg(sendMessage);
                } else {
                    sendMessage.setText("Xatolik,  Iltimos qaytadan urinib ko'ring");
                    adminDTO.setBroadcastMSGStatus(INSPECTION);
                    telegramBotConfig.sendMsg(sendMessage);
                }
                adminDTO.setBroadcastMSGStatus(CHECK_BTN);
            }
            case CHECK_BTN -> {
                String text = "";
                if (message.hasText()) {
                    text = message.getText();
                }
                if (Objects.equals(text, ButtonName.AGAIN_UZ)) {
                    adminDTO.setBroadcastMSGStatus(INSPECTION);
                    return;
                }
                List<BotUsersEntity> all = botUsersRepository.findAll();

                Thread thread = new Thread(() -> {
                    for (BotUsersEntity entity : all) {
                        sendMessage.setChatId(String.valueOf(entity.getTelegramId()));
                        sendMessage.setText("Salom");
                        telegramBotConfig.sendMsg(sendMessage);
                    }
                });
                thread.start();


            }
        }
    }

    private SendPhoto getSendPhoto(Message message) {
        List<PhotoSize> photos = message.getPhoto();
        String caption = message.getCaption();
        // Know file_id
        String f_id = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null)).getFileId();
        // Know photo width
        int f_width = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null)).getWidth();
        // Know photo height
        int f_height = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null)).getHeight();

        SendPhoto msg = new SendPhoto();
        msg.setChatId(String.valueOf(adminId));
        msg.setPhoto(new InputFile(f_id));
        msg.setCaption(caption);
        msg.setReplyMarkup(ButtonUtil.adminBroadcastMsgButton());
        return msg;
    }
}
