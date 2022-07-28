package com.company.bot.service;

import com.company.bot.dto.admin.PhotoDetailDTO;
import com.company.bot.dto.admin.TextDetailDTO;
import com.company.bot.entity.BotUsersEntity;
import com.company.bot.enums.admin.AdminStatus;
import com.company.bot.enums.admin.BroadcastMSGStatus;
import com.company.bot.util.button.ButtonUtil;
import com.company.bot.config.TelegramBotConfig;
import com.company.bot.constants.ButtonName;
import com.company.bot.dto.admin.AdminDTO;
import com.company.bot.repository.BotUsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        adminDTO.setBroadcastMSGStatus(BroadcastMSGStatus.STARTED);
        PhotoDetailDTO.getInstance().clear();
        TextDetailDTO.getInstance().clear();
        sendMessage.setChatId(String.valueOf(adminId));

        sendMessage.setText("Assalomu alaykum, " + message.getFrom().getFirstName() + "" + "\nBoshqaruv bo'limiga xush kelibsiz 😊");
        sendMessage.setReplyMarkup(ButtonUtil.adminMainMenu());
        adminDTO.clear();
        telegramBotConfig.sendMsg(sendMessage);
    }

    public void broadcastAMessage(Message message) {

        var sendMessage = new SendMessage();
        var remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);

        var adminDTO = AdminDTO.getInstance();
        adminDTO.setStatus(AdminStatus.BROADCAST_A_MSG);

        sendMessage.setChatId(String.valueOf(adminId));

        switch (adminDTO.getBroadcastMSGStatus()) {
            case BroadcastMSGStatus.STARTED -> {
                sendMessage.setText("""
                        Barcha foydalanuvchilarga xabar jo'natish uchun, rasm yoki tekst jo'nating
                        """);
                sendMessage.setReplyMarkup(remove);

                telegramBotConfig.sendMsg(sendMessage);
                adminDTO.setBroadcastMSGStatus(BroadcastMSGStatus.INSPECTION);
            }
            case BroadcastMSGStatus.INSPECTION -> {
                if (message.hasPhoto()) {
                    TextDetailDTO.getInstance().clear();

                    SendPhoto msg = getSendPhoto(message);
                    telegramBotConfig.sendMsg(msg);
                } else if (message.hasText()) {
                    PhotoDetailDTO.getInstance().clear();

                    var textDetailDTO = TextDetailDTO.getInstance();
                    textDetailDTO.setText(message.getText());
                    textDetailDTO.setHasText(true);

                    sendMessage.setText(message.getText());
                    sendMessage.setReplyMarkup(ButtonUtil.adminBroadcastMsgButton());

                    telegramBotConfig.sendMsg(sendMessage);
                } else {
                    sendMessage.setText("Xatolik,  Iltimos qaytadan urinib ko'ring");
                    adminDTO.setBroadcastMSGStatus(BroadcastMSGStatus.INSPECTION);
                    telegramBotConfig.sendMsg(sendMessage);
                }
                adminDTO.setBroadcastMSGStatus(BroadcastMSGStatus.CHECK_BTN);
            }
            case BroadcastMSGStatus.CHECK_BTN -> {
                String text = "";
                if (message.hasText()) {
                    text = message.getText();

                }
                switch (text) {
                    case ButtonName.AGAIN_UZ -> {
                        sendMessage.setText("""
                                Barcha foydalanuvchilarga xabar jo'natish uchun, rasm yoki tekst jo'nating
                                """);
                        sendMessage.setReplyMarkup(remove);
                        telegramBotConfig.sendMsg(sendMessage);
                        adminDTO.setBroadcastMSGStatus(BroadcastMSGStatus.INSPECTION);
                    }
                    case ButtonName.SEND -> {
                        var thread = new Thread(this::broadcastAMsg);
                        thread.start();
                    }
                    default -> {
                        sendMessage.setChatId(String.valueOf(adminId));
                        sendMessage.setText("Qaytadan urinib ko'ring!");
                        telegramBotConfig.sendMsg(sendMessage);
                    }
                }
                adminDTO.setBroadcastMSGStatus(BroadcastMSGStatus.STARTED);
                adminDTO.setStatus(null);
            }
            default -> throw new IllegalStateException("Unexpected value: " + adminDTO.getBroadcastMSGStatus());
        }
    }

    public void handleStats() {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(adminId));
        sendMessage.setText(String.format("""
                        <b>Bot statistikasi bilan tanishing:</b>
                                                
                        Bugun qo'shilganlar soni: <b>%d - ta </b>
                        Oxirgi 3 kuni ichida qo'shilganlar: <b>%d - ta </b>
                        Oxirgi 1 oyda qo'shilganlar: <b>%d - ta </b>
                                                
                        👥 Jami foydalanuvchilar soni: <b>%d - ta </b>
                        """,
                botUsersRepository.joinedToday(),
                botUsersRepository.joinedLastThreeDays(),
                botUsersRepository.joinedLastOneMonth(),
                botUsersRepository.countAllUsers()));
        sendMessage.setParseMode("HTML");
        telegramBotConfig.sendMsg(sendMessage);
    }

    @SneakyThrows
    private void broadcastAMsg() {
        var textDetailDTO = TextDetailDTO.getInstance();
        var photoDetailDTO = PhotoDetailDTO.getInstance();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(adminId));

        var editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(adminId));

        Instant start = Instant.now();

        sendMessage.setText("⏳");

        Message message = telegramBotConfig.execute(sendMessage);
        editMessageText.setMessageId(message.getMessageId());

        List<BotUsersEntity> all = botUsersRepository.findAll();

        if (!all.isEmpty()) {
            if (photoDetailDTO.isHasPhoto()) {
                var sendPhoto = new SendPhoto();

                sendPhoto.setPhoto(new InputFile(photoDetailDTO.getFileId()));
                sendPhoto.setCaption(photoDetailDTO.getCaption());

                all.forEach(botUsersEntity -> {
                    sendPhoto.setChatId(String.valueOf(botUsersEntity.getTelegramId()));
                    telegramBotConfig.sendMsg(sendPhoto);
                });
            } else if (textDetailDTO.isHasText()) {
                all.forEach(botUsersEntity -> {
                    sendMessage.setChatId(String.valueOf(botUsersEntity.getTelegramId()));
                    sendMessage.setText(textDetailDTO.getText());
                    telegramBotConfig.sendMsg(sendMessage);
                });
            }
        } else {
            editMessageText.setText("Botda hali aktiv foydalanuvchilar mavjud emas");
            telegramBotConfig.sendMsg(editMessageText);
            return;
        }

        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);

        long minute = timeElapsed.getSeconds() / 60;
        sendMessage.setChatId(String.valueOf(adminId));

        editMessageText.setText(minute > 1 ? "Xabaringiz <b>" + minute + " daqiqada </b>foydalanuvchilarga tarqatildi"
                : "Xabaringiz <b>" + timeElapsed.getSeconds() + " soniyada </b>foydalanuvchilarga tarqatildi");

        editMessageText.setParseMode("HTML");
        telegramBotConfig.sendMsg(editMessageText);

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
