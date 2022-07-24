package com.company.service;

import com.company.config.TelegramBotConfig;
import com.company.dto.*;
import com.company.entity.*;
import com.company.enums.Gender;
import com.company.enums.LanguageCode;
import com.company.enums.UserQuestionnaireStatus;
import com.company.enums.UserStatus;
import com.company.repository.ComplaintsInfoRepository;
import com.company.repository.ComplaintsRepository;
import com.company.repository.DrugsPhotoRepository;
import com.company.repository.InspectionPhotoRepository;
import com.company.util.button.ButtonUtil;
import com.company.util.button.InlineButtonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

import static com.company.config.TelegramBotConfig.*;
import static com.company.constants.ButtonName.*;
import static com.company.enums.LanguageCode.RU;
import static com.company.enums.LanguageCode.UZ;
import static com.company.enums.UserQuestionnaireStatus.*;
import static com.company.enums.UserStatus.ACTIVE;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallBackQueryService {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final BotUsersService botUsersService;
    private final ComplaintsService complaintsService;
    private final ComplaintsRepository complaintsRepository;
    private final ComplaintsInfoRepository complaintsInfoRepository;
    private final DrugsPhotoRepository drugsPhotoRepository;
    private final InspectionPhotoRepository inspectionPhotoRepository;
    private final GeneratePdfService generatePdfService;

    public void handleLangCodeUZ(Message message, User user) {
        var dto = USER_LIST.get(user.getId());

        dto.setLanguageCode(UZ);
        USER_LIST.put(user.getId(), dto);
        var deleteMessage = new DeleteMessage(String.valueOf(message.getChatId()), message.getMessageId());

        telegramBotConfig.sendMsg(deleteMessage);

        var sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Asosiy bo'limga xush keldingiz 😊");
        sendMessage.setReplyMarkup(ButtonUtil.keyboard(UZ));

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void handleLangCodeRU(Message message, User user) {
        var dto = USER_LIST.get(user.getId());
        dto.setLanguageCode(RU);
        USER_LIST.put(user.getId(), dto);

        var deleteMessage = new DeleteMessage(String.valueOf(message.getChatId()), message.getMessageId());

        telegramBotConfig.sendMsg(deleteMessage);

        var sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Добро пожаловать в основной раздел \uD83D\uDE0A");
        sendMessage.setReplyMarkup(ButtonUtil.keyboard(RU));

        telegramBotConfig.sendMsg(sendMessage);
    }

    public void handleGenderMale(Message message, User user) {
        var dto = USER_LIST.get(user.getId());

        dto.setGender(Gender.MALE);
        dto.setQuestionnaireStatus(UserQuestionnaireStatus.HEIGHT);

        USER_LIST.put(user.getId(), dto);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setText("✅ Muvaffaqiyatli / ✅ Успешный");
        telegramBotConfig.sendMsg(editMessageText);


        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));

        switch (dto.getLanguageCode()) {
            case UZ -> {
                sendMessage.setText("Bo'yingiz uzunligini kiriting.\nNamuna: (175-sm) ");
                telegramBotConfig.sendMsg(sendMessage);
            }
            case RU -> {
                sendMessage.setText("укажите свой рост. \nПример: (175 см)");
                telegramBotConfig.sendMsg(sendMessage);
            }
        }
    }

    public void changeLang(Message message, String data){
        var user =USER_LIST.get(message.getChatId());
        user.setLanguageCode(LanguageCode.valueOf(data));
        USER_LIST.put(message.getChatId(), user);
        handleCallBackConfirm(message);

    }

    public void handleGenderFemale(Message message, User user) {
        var dto = USER_LIST.get(user.getId());

        dto.setGender(Gender.FEMALE);
        dto.setQuestionnaireStatus(UserQuestionnaireStatus.HEIGHT);

        USER_LIST.put(user.getId(), dto);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setText("✅ Muvaffaqiyatli / ✅ Успешный");
        telegramBotConfig.sendMsg(editMessageText);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));

        switch (dto.getLanguageCode()) {
            case UZ -> {
                sendMessage.setText("Bo'yingiz uzunligini kiriting.\nNamuna: (175-sm) ");
                telegramBotConfig.sendMsg(sendMessage);
            }
            case RU -> {
                sendMessage.setText("укажите свой рост. \nПример: (175 см)");
                telegramBotConfig.sendMsg(sendMessage);
            }
        }
    }

    public void handleCallBackConfirm(Message message) {

        var delete = new DeleteMessage();
        delete.setMessageId(message.getMessageId());
        delete.setChatId(String.valueOf(message.getChatId()));
        telegramBotConfig.sendMsg(delete);
        delete.setMessageId(message.getMessageId() - 1);
        telegramBotConfig.sendMsg(delete);

        var dto = USER_LIST.get(message.getChatId());
        dto.setStatus(ACTIVE);
        USER_LIST.put(message.getChatId(), dto);

        save(dto, message.getChatId());

        var editMessageText = new SendMessage();

        editMessageText.setChatId(String.valueOf(message.getChatId()));
        if (dto.getLanguageCode().equals(UZ)) {
            editMessageText.setText("Bosh menu!");
            editMessageText.setReplyMarkup(ButtonUtil.complaintsMenu(UZ));
        } else {
            editMessageText.setText("Главное меню!");
            editMessageText.setReplyMarkup(ButtonUtil.complaintsMenu(RU));
        }
        telegramBotConfig.sendMsg(editMessageText);
        System.out.println(USER_LIST.get(message.getChatId()));
    }

    public void menu(Message message) {
        var dto = USER_LIST.get(message.getChatId());

        var editMessageText = new SendMessage();

        editMessageText.setChatId(String.valueOf(message.getChatId()));
        if (dto.getLanguageCode().equals(UZ)) {
            editMessageText.setText("Bosh menu");
            editMessageText.setReplyMarkup(ButtonUtil.complaintsMenu(UZ));
        } else {
            editMessageText.setText("Информация получена ✅");
            editMessageText.setReplyMarkup(ButtonUtil.complaintsMenu(RU));
        }
        telegramBotConfig.sendMsg(editMessageText);
    }

    public void handleCallBackAgain(Message message, User user) {

        var dto = USER_LIST.get(user.getId());

        dto.setStatus(UserStatus.NOT_ACTIVE);
        dto.setQuestionnaireStatus(UserQuestionnaireStatus.NAME);

        USER_LIST.put(message.getChatId(), dto);

        var deleteMessage = new DeleteMessage();

        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(String.valueOf(message.getChatId()));

        telegramBotConfig.sendMsg(deleteMessage);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        switch (dto.getLanguageCode()) {
            case UZ -> {
                sendMessage.setText("Iltimos, yana qaytadan ma'lumotlaringizni kiritib chiqing");
                sendMessage.setReplyMarkup(ButtonUtil.fillFormButton(UZ));
            }
            case RU -> {
                sendMessage.setText("Пожалуйста, введите ваши данные еще раз");
                sendMessage.setReplyMarkup(ButtonUtil.fillFormButton(RU));
            }
        }
        telegramBotConfig.sendMsg(sendMessage);
    }

    public void startComplaintsInfoQuestionUz(Message message, BotUsersDTO user) {
        var userCoplaints = USER_COMPLAINT.get(message.getChatId());
        if (!userCoplaints.isEmpty()) {
            complaintsService.fieldSave(userCoplaints, message.getChatId());
        }
        var delete = new DeleteMessage();
        delete.setChatId(String.valueOf(message.getChatId()));
        delete.setMessageId(message.getMessageId());
        telegramBotConfig.sendMsg(delete);
        var remove = new ReplyKeyboardRemove();

        remove.setRemoveKeyboard(true);

        USER_COMPLAINT_INFO.put(message.getChatId(), new ComplaintsInfoDTO());
        var sendMsg = new SendMessage();
        user.setQuestionnaireStatus(COMPLAINTS_INFO_WRITE);
        USER_LIST.put(message.getChatId(), user);
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        sendMsg.setReplyMarkup(remove);
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Murojaatga sabab bo’lgan shikoyatlarni yozing yoki ovozli xabar yuboring: ");
        else
            sendMsg.setText("Напишите или отправьте голосовое сообщение о жалобах, которые привели к обращению: ");
        telegramBotConfig.sendMsg(sendMsg);
    }

    public void cigarette(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        var user = USER_LIST.get(message.getChatId());
        var data = callbackQuery.getData();
        var infoDTO = USER_COMPLAINT_INFO.get(message.getChatId());

        switch (user.getLanguageCode()) {
            case UZ -> {
                switch (data) {
                    case CIGARETTA_NO_UZ -> infoDTO.setCigarette(CIGARETTA_NO_UZ);
                    case CIGARETTA_05_1_UZ -> infoDTO.setCigarette(CIGARETTA_05_1_UZ);
                    case CIGARETTA_1_2_UZ -> infoDTO.setCigarette(CIGARETTA_1_2_UZ);
                }
            }
            case RU -> {
                switch (data) {
                    case CIGARETTA_NO_RU -> infoDTO.setCigarette(CIGARETTA_NO_RU);
                    case CIGARETTA_05_1_RU -> infoDTO.setCigarette(CIGARETTA_05_1_RU);
                    case CIGARETTA_1_2_RU -> infoDTO.setCigarette(CIGARETTA_1_2_RU);
                }
            }
        }


        var delete = new DeleteMessage();
        delete.setMessageId(message.getMessageId());
        delete.setChatId(String.valueOf(message.getChatId()));
        telegramBotConfig.sendMsg(delete);

        user.setQuestionnaireStatus(UserQuestionnaireStatus.DISEASES_LIST);
        USER_LIST.put(message.getChatId(), user);
        USER_COMPLAINT_INFO.put(message.getChatId(), infoDTO);


        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
            sendMsg.setText("Hozirda qaysi kasalliklarga davolanyapsiz? ");
        else
            sendMsg.setText("От каких заболеваний вы сейчас лечитесь?");

        telegramBotConfig.sendMsg(sendMsg);
    }

    public void result(Message message) {
        var dto = USER_COMPLAINT_INFO.get(message.getChatId());
        var lang = USER_LIST.get(message.getChatId()).getLanguageCode();
        var builder = new StringBuilder();
        if (lang.equals(UZ)) {
            builder.append("<b>🔎 Iltimos, o'z ma'lumotlaringizni tekshirib chiqing.</b>\n");

            if (!dto.getCauseOfComplaint().startsWith("https")) {
                builder.append("Murojatga sabab bo’lgan shikoyatlar: ").append(dto.getCauseOfComplaint()).append("\n");
            }
            builder.append("Shikoyatlar boshlangan vaqt: ").append(dto.getComplaintStartedTime()).append("\n");
            if (dto.getCauseOfComplaint()!=null) {
                builder.append("Qabul qilgan va qilayotgan dorilar: ").append(dto.getDrugsList()).append("\n");
            }
            switch (dto.getCigarette()){
                case CIGARETTA_NO_UZ-> builder.append("Sigaret: chekmayman" + "\n");
                case CIGARETTA_05_1_UZ-> builder.append("Sigaret: 0.5-1 pachka" + "\n");
                case CIGARETTA_1_2_UZ-> builder.append("Sigaret: 1-2 pachka" + "\n");
            }
        } else {
            builder.append("<b>🔎 Пожалуйста, проверьте вашу информацию: </b>\n");

            if (!dto.getCauseOfComplaint().startsWith(" https")) {
                builder.append("Жалобы, которые привели к обращению: " + dto.getCauseOfComplaint() + "\n");
            }
            builder.append("Когда начались жалобы: " + dto.getComplaintStartedTime() + "\n");
            if (!dto.getCauseOfComplaint().isEmpty()) {
                builder.append("Лекарства, которые вы принимали и принимаете: " + dto.getDrugsList() + "\n");
            }
            switch (dto.getCigarette()){
                case CIGARETTA_NO_RU-> builder.append("Сигареты: не курю" + "\n");
                case CIGARETTA_05_1_RU-> builder.append("Сигарета: 0,5-1 пачка" + "\n");
                case CIGARETTA_1_2_RU-> builder.append("Сигареты: 1-2 пачки" + "\n");
            }
            builder.append("Заболевания, от которых вы сейчас лечитесь:" + dto.getDiseasesList() + "\n");
        }

        var remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        var tempMsg = new SendMessage();
        tempMsg.setChatId(String.valueOf(message.getChatId()));
        tempMsg.setText("...");
        tempMsg.setReplyMarkup(remove);
        int id;
        try {
            id = telegramBotConfig.execute(tempMsg).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        var delete = new DeleteMessage();
        delete.setChatId(String.valueOf(message.getChatId()));
        delete.setMessageId(id);
        telegramBotConfig.sendMsg(delete);


        /*var str = String.format("""
                        <b>🔎 Iltimos, o'z ma'lumotlaringizni tekshirib chiqing.</b>
                                                    
                        <i>Murojatga sabab bo’lgan shikoyatlar: </i> %s
                        <i>Shikoyatlar boshlangan vaqt: </i> %s
                        <i>Qabul qilgan va qilayotgan dorilar: </i> %s
                        <i>Sigaret: </i> %s
                        <i>Hozirda davolanayotgan kasalliklar: </i> %s
                                                
                        """,
                dto.getCauseOfComplaint(), dto.getComplaintStartedTime(),
                dto.getDrugsList(), dto.getCigarette(),
                dto.getDiseasesList());*/

        var sendMsg = new SendMessage();
        sendMsg.setParseMode("HTML");
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        sendMsg.setText(builder.toString());
        sendMsg.setReplyMarkup(InlineButtonUtil.confirmComplints(lang));
        telegramBotConfig.sendMsg(sendMsg);

    }

    public void confirm(Message message) {
        var id = message.getChatId();
        var comlaintsList = USER_COMPLAINT.get(id);
        var infoDto = USER_COMPLAINT_INFO.get(id);
        var drugs_photo_list = USER_PHOTOS_DRUGS.get(id);
        var inspection_photo_list = USER_PHOTOS_INSPECTION.get(id);
        var user=USER_LIST.get(id);
        user.setStatus(ACTIVE);
        USER_LIST.put(id, user);

        if (!comlaintsList.isEmpty()) {
            for (ComplaintsDTO dto : comlaintsList) {
                var entity = new ComplaintsEntity();
                entity.setUserId(id);
                entity.setNameUz(dto.getNameUz());
                entity.setNameRu(dto.getNameRu());
                entity.setKey(dto.getKey());
                entity.setCreatedDate(LocalDate.now());
                complaintsRepository.save(entity);
            }
        }

        if (infoDto != null) {
            var entity = new ComplaintsInfoEntity();
            entity.setUserId(id);
            entity.setCauseOfComplaint(infoDto.getCauseOfComplaint());
            entity.setComplaintStartedTime(infoDto.getComplaintStartedTime());
            if (infoDto.getDrugsList() != null)
                entity.setDrugsList(infoDto.getDrugsList());
            entity.setCigarette(infoDto.getCigarette());
            entity.setDiseasesList(infoDto.getDiseasesList());
            if (infoDto.getInspectionPapers() != null)
                entity.setInspectionPapers(infoDto.getInspectionPapers());
            complaintsInfoRepository.save(entity);
        }

        if (drugs_photo_list != null) {
            for (UserPhotoDTO dto : drugs_photo_list) {
                var entity = new DrugsPhotoEntity();
                entity.setUserId(id);
                entity.setFielId(dto.getFileId());
                entity.setLink(dto.getLink());
                drugsPhotoRepository.save(entity);
            }
        }

        if (inspection_photo_list != null) {
            for (UserPhotoDTO dto : inspection_photo_list) {
                var entity = new InspectionPhotoEntity();
                entity.setUserId(id);
                entity.setFielId(dto.getFileId());
                entity.setLink(dto.getLink());
                inspectionPhotoRepository.save(entity);
            }
        }
        generatePdfService.createPdf(new PdfDTO(
                user,
                infoDto,
                comlaintsList,
                drugs_photo_list,
                inspection_photo_list
        ));

        var delete =new DeleteMessage();
        delete.setMessageId(message.getMessageId());
        delete.setChatId(String.valueOf(message.getChatId()));
        telegramBotConfig.sendMsg(delete);

        var sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(message.getChatId()));
        if (user.getLanguageCode().equals(UZ))
        sendMsg.setText(" Malumotlar qabul qilindi!\n Sizga aloqaga chiqamiz!");
        else
            sendMsg.setText(" Информация получена!\n Мы свяжемся с вами!");

        sendMsg.setReplyMarkup(ButtonUtil.menu(user.getLanguageCode()));

        telegramBotConfig.sendMsg(sendMsg);
    }

    public void again(Message message) {
        USER_COMPLAINT_INFO.remove(message.getChatId());
        USER_PHOTOS_INSPECTION.remove(message.getChatId());
        USER_PHOTOS_DRUGS.remove(message.getChatId());
    }

    public void backButton(Message message) {
        var user = USER_LIST.get(message.getChatId());
        user.setQuestionnaireStatus(DEFAULT);
        USER_LIST.put(message.getChatId(), user);
        var lang = user.getLanguageCode();


        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        if (lang.equals(UZ))
            sendMessage.setText("Kerakli bo'limni tanlang!.");
        else
            sendMessage.setText("Выберите нужный раздел!.");
        sendMessage.setReplyMarkup(ButtonUtil.backButtonClick(lang));
        telegramBotConfig.sendMsg(sendMessage);
    }


    private void save(BotUsersDTO dto, long tgId) {
        var entity = new BotUsersEntity();
        entity.setStatus(ACTIVE);
        entity.setTelegramId(tgId);
        entity.setGender(dto.getGender());
        entity.setBirthDate(dto.getBirthDate());
        entity.setHeight(dto.getHeight());
        entity.setWeight(dto.getWeight());
        entity.setPhone(dto.getPhone());
        entity.setName(dto.getName());
        entity.setLanguageCode(dto.getLanguageCode());
        entity.setSurname(dto.getSurname());
        entity.setBloodPrassure(dto.getBloodPrassure());
        entity.setDiabets(dto.getDiabets());
        entity.setTemprature(dto.getTemprature());
        entity.setHeartBeat(dto.getHeartBeat());
        botUsersService.saveUser(entity);
    }


}
