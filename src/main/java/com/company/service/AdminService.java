package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.constants.ButtonName;
import com.company.dto.admin.AdminDTO;
import com.company.dto.admin.PhotoDetailDTO;
import com.company.dto.admin.TextDetailDTO;
import com.company.entity.BotUsersEntity;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.company.enums.admin.AdminStatus.BROADCAST_A_MSG;
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
                    var textDetailDTO = TextDetailDTO.getInstance();
                    textDetailDTO.setText(message.getText());
                    textDetailDTO.setHasText(true);

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
                    sendMessage.setText("""
                            Barcha foydalanuvchilarga xabar jo'natish uchun, rasm yoki tekst jo'nating
                            """);
                    sendMessage.setReplyMarkup(remove);

                    telegramBotConfig.sendMsg(sendMessage);
                    adminDTO.setBroadcastMSGStatus(INSPECTION);
                } else if (Objects.equals(text, ButtonName.SEND)) {
                    var thread = new Thread(this::broadcastAMsg);
                    thread.start();
                } else {
                    sendMessage.setChatId(String.valueOf(adminId));
                    sendMessage.setText("Qaytadan urinib ko'ring!");
                    telegramBotConfig.sendMsg(sendMessage);
                }
                adminDTO.setBroadcastMSGStatus(STARTED);
                adminDTO.setStatus(null);
            }
            default -> throw new IllegalStateException("Unexpected value: " + adminDTO.getBroadcastMSGStatus());
        }
    }

    private void broadcastAMsg() {
        var textDetailDTO = TextDetailDTO.getInstance();
        var photoDetailDTO = PhotoDetailDTO.getInstance();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(adminId));

        Instant start = Instant.now();

        sendMessage.setText("⏳");
        telegramBotConfig.sendMsg(sendMessage);

        List<BotUsersEntity> all = botUsersRepository.findAll();
        if (all.isEmpty()) {
            sendMessage.setText("Botda hali aktiv foydalanuvchilar mavjud emas");
            telegramBotConfig.sendMsg(sendMessage);
            return;
        }
        if (photoDetailDTO.isHasPhoto()) {
            var sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(new InputFile(photoDetailDTO.getFileId()));
            sendPhoto.setCaption(photoDetailDTO.getCaption());
            all.forEach(botUsersEntity -> {
                sendPhoto.setChatId(String.valueOf(botUsersEntity.getTelegramId()));
                telegramBotConfig.sendMsg(sendPhoto);
            });
        }
        if (textDetailDTO.isHasText()) {
            all.forEach(botUsersEntity -> {
                sendMessage.setChatId(String.valueOf(botUsersEntity.getTelegramId()));
                sendMessage.setText(textDetailDTO.getText());
                telegramBotConfig.sendMsg(sendMessage);
            });
        }

        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);

        long minute = timeElapsed.getSeconds() / 60;
        sendMessage.setChatId(String.valueOf(adminId));

        if (minute > 1)
            sendMessage.setText("Xabaringiz <b>" + minute + " daqiqada </b>foydalanuvchilarga tarqatildi");
        else
            sendMessage.setText("Xabaringiz <b>" + timeElapsed.getSeconds() + " soniyada </b>foydalanuvchilarga tarqatildi");

        sendMessage.setParseMode("HTML");
        telegramBotConfig.sendMsg(sendMessage);

        textDetailDTO.clear();
        photoDetailDTO.clear();

    }

    private SendPhoto getSendPhoto(Message message) {
        var photo = PhotoDetailDTO.getInstance();

        List<PhotoSize> photos = message.getPhoto();
        String caption = message.getCaption();

        String f_id = Objects.requireNonNull(photos.stream().max(Comparator
                .comparing(PhotoSize::getFileSize)).orElse(null)).getFileId();

        photo.setFileId(f_id);
        photo.setCaption(caption);
        photo.setHasPhoto(true);

        var msg = new SendPhoto();
        msg.setChatId(String.valueOf(adminId));
        msg.setPhoto(new InputFile(f_id));
        msg.setCaption(caption);
        msg.setReplyMarkup(ButtonUtil.adminBroadcastMsgButton());
        return msg;
    }
}
