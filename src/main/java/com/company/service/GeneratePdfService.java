package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.entity.BotUsersEntity;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class GeneratePdfService {
    @Value("${attach.upload.folder}")
    private String attachFolder;
    @Value("${channel.service.name}")
    private String serviceChannelId;
    @Lazy
    private final TelegramBotConfig telegramBotConfig;

    public void createPdf(BotUsersEntity entity) {
        var document = new Document();
        var attach = new File(attachFolder);
        if (!attach.exists()) attach.mkdirs();

        var FILE_NAME = attachFolder + entity.getTelegramId() + ".pdf";
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE_NAME));
            document.open();
            var font = new Font();
            font.setStyle(Font.BOLD);
            font.setSize(24);

            var title = new Paragraph();
            title.setFont(font);
            title.add(entity.getName() + " " + entity.getSurname());
            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            var p2 = new Paragraph();
            p2.add(getElements(entity));
            document.add(p2);
            document.close();
            System.out.println("Done");

            var file = new File(FILE_NAME);
            export(file);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public void export(File mediaFile) {
        var sendDocument = new SendDocument();
        sendDocument.setChatId(serviceChannelId);
        sendDocument.setDocument(new InputFile(mediaFile));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formatDateTime = now.format(formatter);

        sendDocument.setCaption(formatDateTime);
        telegramBotConfig.sendMsg(sendDocument);
        mediaFile.delete();
        System.out.println("Success");
    }

    private String getElements(BotUsersEntity entity) {
        return "Telefon raqami: " + entity.getPhone() +
                "\nTug'ilgan kuni: " + entity.getBirthDate() +
                "\nJinsi: " + entity.getGender() +
                "\nBo'yi: " + entity.getHeight() +
                "\nVazni: " + entity.getWeight() +
                "\nHarorati: " + entity.getCurrentTemperature();
    }
}
