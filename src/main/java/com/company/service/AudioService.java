package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.dto.ComplaintsInfoDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.company.config.TelegramBotConfig.USER_COMPLAINT_INFO;
import static com.company.config.TelegramBotConfig.USER_LIST;
import static com.company.enums.LanguageCode.UZ;
import static com.company.enums.UserQuestionnaireStatus.COMPLAINTS_STARTED_TIME;

@Component
@AllArgsConstructor
public class AudioService {
    private TelegramBotConfig telegramBotConfig;

    public void  getAudio(Message message) {
        var user = TelegramBotConfig.USER_LIST.get(message.getChatId());

        var voice = message.getVoice();


        var voiceMsg = new SendVoice();
        voiceMsg.setChatId("-1001788256915");
        voiceMsg.setCaption("name: "+user.getName());
        voiceMsg.setDuration(voice.getDuration());
        voiceMsg.setVoice(new InputFile(voice.getFileId()));


        Message tempMessage = null;
        try {
            tempMessage = telegramBotConfig.execute(voiceMsg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        String link = "https://t.me/c/1788256915/"+tempMessage.getMessageId();

        var dto = USER_COMPLAINT_INFO.get(message.getChatId());
        dto.setCauseOfComplaint(link);

        USER_COMPLAINT_INFO.put(message.getChatId(), dto);

        user.setQuestionnaireStatus(COMPLAINTS_STARTED_TIME);
        USER_LIST.put(message.getChatId(), user);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Shikoyatlar qachon boshlandi?");
        else
            sendMsg.setText("Когда начались жалобы?");
        telegramBotConfig.sendMsg(sendMsg);

       /* var sendMsg = new SendMessage();
        sendMsg.setText(link + tempMessage.getMessageId());
        sendMsg.setChatId();
        telegramBotConfig.sendMsg(sendMsg);*/
    }
}
