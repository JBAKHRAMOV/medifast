package com.company.service;

import com.company.entity.BotUsersEntity;
import com.lowagie.text.*;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

@Service
public class PdfGeneratorService {
    @Value("${attach.upload.folder}")
    private String attachFolder = "storage/";

    public void export(BotUsersEntity entity) {
        try {
            Document document = new Document(PageSize.A4);

            String filename = attachFolder + entity.getTelegramId() + ".html";

            var fileOutputStream = new FileOutputStream(filename);
            var outputStream = new ObjectOutputStream(fileOutputStream);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            var fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(18);

            var title = new Paragraph(getTitle(entity), fontTitle);
            title.setAlignment(Paragraph.ALIGN_CENTER);


            var fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
            fontParagraph.setSize(12);

            Paragraph elements = new Paragraph(getElements(entity), fontParagraph);
            elements.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(title);
            document.add(elements);


            document.close();
            fileOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTitle(BotUsersEntity entity) {
        return entity.getName() + " " + entity.getSurname();
    }

    private String getElements(BotUsersEntity entity) {
        return "Telefon raqami: " + entity.getPhone() +
                "\n<b>Tug'ilgan kuni: </b>" + entity.getBirthDate() +
                "\nJinsi: " + entity.getGender() +
                "\nBo'yi: " + entity.getHeight() +
                "\nVazni: " + entity.getWeight() +
                "\nHarorati: " + entity.getCurrentTemperature();
    }
}
