package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.dto.BotUsersDTO;
import com.company.dto.ComplaintsDTO;
import com.company.dto.ComplaintsInfoDTO;
import com.company.dto.PdfDTO;
import com.company.enums.LanguageCode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratePdfService {
    @Value("${attach.upload.folder}")
    private String attachFolder;
    @Value("${channel.service.name}")
    private String serviceChannelId;
    @Lazy
    private final TelegramBotConfig telegramBotConfig;

    public void createPdf(PdfDTO dto) {
        var document = new Document();
        var attach = new File(attachFolder);
        if (!attach.exists()) attach.mkdirs();

        var FILE_NAME = attachFolder + dto.getUser().getTelegramId() + ".pdf";
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE_NAME));
            document.open();
            String FONT_FILENAME = "assets/arial.ttf";
            BaseFont bf = BaseFont.createFont(FONT_FILENAME, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            var font = new Font(bf, 12f, Font.NORMAL);
            var linkFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLUE);

            var title = new Paragraph();
            title.setFont(font);
            title.add(dto.getUser().getName() + " " + dto.getUser().getSurname());
            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            var p2 = new Paragraph();
            p2.setFont(font);
            p2.add(getUserElements(dto.getUser()));
            if (dto.getComplaintsInfoDTO().getCauseOfComplaint().startsWith("https://t.me/")) {
                var paragraph = new Paragraph();
                paragraph.add("\nMurojaat sabablari: ");
                var phrase = new Phrase();
                Chunk chunk = new Chunk();
                chunk.append("ovozli xabarga havola");
                chunk.setAnchor(dto.getComplaintsInfoDTO().getCauseOfComplaint());
                phrase.setFont(linkFont);
                phrase.add(chunk);
                document.add(paragraph);
                document.add(phrase);
            } else {
                p2.add("\nMurojaat sabablari: " + dto.getComplaintsInfoDTO().getCauseOfComplaint());
            }
            p2.add(getComplaintsElements(dto.getComplaintsInfoDTO()));
            p2.add(getComplaintsList(dto.getComplaintsList(), dto.getUser().getLanguageCode()));
            document.add(p2);
            if (dto.getDrugsList() != null) {
                var p3 = new Paragraph();
                p3.add("\nQabul qilayotgan dorilari: ");
                document.add(p3);
                dto.getDrugsList().forEach(userPhotoDTO -> {
                    var phrase = new Phrase();
                    Chunk chunk = new Chunk();
                    chunk.append("suratga havola, ");
                    chunk.setAnchor(userPhotoDTO.getLink());
                    phrase.setFont(linkFont);
                    phrase.add(chunk);
                    try {
                        document.add(phrase);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                });
            }
            if (dto.getInspectionList() != null) {
                var p4 = new Paragraph();
                p4.add("\nTekshiruv qog'ozlari: ");
                document.add(p4);
                dto.getInspectionList().forEach(userPhotoDTO -> {
                    var phrase = new Phrase();
                    Chunk chunk = new Chunk();
                    chunk.append("suratga havola, ");
                    chunk.setAnchor(userPhotoDTO.getLink());
                    phrase.setFont(linkFont);
                    phrase.add(chunk);
                    try {
                        document.add(phrase);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                });
            }
            document.close();

            var file = new File(FILE_NAME);
            export(file, dto.getUser());

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public void export(File mediaFile, BotUsersDTO dto) {
        var sendDocument = new SendDocument();
        sendDocument.setChatId(serviceChannelId);
        sendDocument.setDocument(new InputFile(mediaFile));

        sendDocument.setCaption(dto.getName() + " " + dto.getSurname());
        telegramBotConfig.sendMsg(sendDocument);
        mediaFile.delete();
    }

    private String getUserElements(BotUsersDTO dto) {
        return "Telefon raqami: " + dto.getPhone() + "\nTug'ilgan kuni: " + dto.getBirthDate() + "\nJinsi: " + dto.getGender() + "\nBo'yi: " + dto.getHeight() + "\nVazni: " + dto.getWeight();
    }

    private String getComplaintsElements(ComplaintsInfoDTO dto) {
        StringBuilder txt = new StringBuilder();
        txt.append("\nShikoyat qachon boshlandi: ").append(dto.getComplaintStartedTime());
        if (dto.getDrugsList() != null)
            txt.append("\nQabul qilgan yoki qilayotgan dorilar ro'yxati: ").append(dto.getDrugsList());
        txt.append("\nSigaret chekadimi: ").append(dto.getCigarette());
        txt.append("\nQanaqa kasalliklarga qarshi davolanyapti: ").append(dto.getDiseasesList());
        return txt.toString();
    }

    private String getComplaintsList(List<ComplaintsDTO> complaintsList, LanguageCode languageCode) {
        StringBuilder txt = new StringBuilder();
        if (!complaintsList.isEmpty()) {
            txt.append("\nShikoyatlar ro'yxati: ");
            complaintsList.forEach(complaintsDTO -> {
                switch (languageCode) {
                    case UZ -> txt.append(complaintsDTO.getNameUz()).append(", ");
                    case RU -> txt.append(complaintsDTO.getNameRu()).append(", ");
                }
            });
        }

        return txt.toString();
    }
}

