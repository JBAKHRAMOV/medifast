package com.company.bot.service;

import com.company.bot.config.TelegramBotConfig;
import com.company.bot.dto.UserPhotoDTO;
import com.company.bot.util.button.ButtonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedList;
import java.util.List;

import static com.company.bot.config.TelegramBotConfig.*;
import static com.company.bot.enums.LanguageCode.UZ;

@Component
@RequiredArgsConstructor
public class PhotoServise {

    private final TelegramBotConfig telegramBotConfig;

    @Value("${channel.storage.name}")
    private String channelId;

    public void drugsPhotoSave(Message message) {

        var photo = message.getPhoto().get(0);

        var sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(photo.getFileId()));
        sendPhoto.setCaption("name: " + USER_LIST.get(message.getChatId()).getName());
        sendPhoto.setChatId("-100"+channelId);

        Message tempMessage;
        try {
            tempMessage = telegramBotConfig.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        String link = "https://t.me/c/"+channelId+"/" + tempMessage.getMessageId();

        List<UserPhotoDTO> list=new LinkedList<>();
        if (USER_PHOTOS_DRUGS.containsKey(message.getChatId())){
            list=USER_PHOTOS_DRUGS.get(message.getChatId());
        }
        var dto = new UserPhotoDTO();
        dto.setFileId(photo.getFileId());
        dto.setLink(link);

        list.add(dto);
        USER_PHOTOS_DRUGS.put(message.getChatId(), list);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (USER_LIST.get(message.getChatId()).getLanguageCode().equals(UZ))
            sendMsg.setText("Barcha rasmlarni jo'natib bolganingizdan so'ng tugatish tugmasini bosing!");
        else
            sendMsg.setText("После того, как вы загрузили все изображения, нажмите «Готово»!");
        sendMsg.setReplyMarkup(ButtonUtil.photoNext(USER_LIST.get(message.getChatId()).getLanguageCode()));
        telegramBotConfig.sendMsg(sendMsg);
    }

    public void inspectionPhotoSave(Message message) {

        var photo = message.getPhoto().get(0);

        var sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(photo.getFileId()));
        sendPhoto.setCaption("name: " + USER_LIST.get(message.getChatId()).getName());
        sendPhoto.setChatId("-100"+channelId);

        Message tempMessage;
        try {
            tempMessage = telegramBotConfig.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        String link = "https://t.me/c/"+channelId+"/" + tempMessage.getMessageId();

        List<UserPhotoDTO> list=new LinkedList<>();
        if (USER_PHOTOS_INSPECTION.containsKey(message.getChatId())){
            list=USER_PHOTOS_INSPECTION.get(message.getChatId());
        }
        var dto = new UserPhotoDTO();
        dto.setFileId(photo.getFileId());
        dto.setLink(link);

        list.add(dto);
        USER_PHOTOS_INSPECTION.put(message.getChatId(), list);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (USER_LIST.get(message.getChatId()).getLanguageCode().equals(UZ))
            sendMsg.setText("Barcha rasmlarni jo'natib bolganingizdan so'ng tugatish tugmasini bosing!");
        else
            sendMsg.setText("После того, как вы загрузили все изображения, нажмите «Готово»!");
        sendMsg.setReplyMarkup(ButtonUtil.photoNext(USER_LIST.get(message.getChatId()).getLanguageCode()));
        telegramBotConfig.sendMsg(sendMsg);
    }

}


























