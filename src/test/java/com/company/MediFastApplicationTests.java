package com.company;

import com.company.entity.BotUsersEntity;
import com.company.enums.Gender;
import com.company.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class MediFastApplicationTests {

    @Test
    void contextLoads() {
        BotUsersEntity botUsersEntity = new BotUsersEntity();
        botUsersEntity.setTelegramId(123456789L);
        botUsersEntity.setName("Ali");
        botUsersEntity.setSurname("Valiyev");
        botUsersEntity.setPhone("+998932158000");
        botUsersEntity.setBirthDate(LocalDate.now());
        botUsersEntity.setHeight("170-sm");
        botUsersEntity.setGender(Gender.MALE);
        botUsersEntity.setWeight("90-kg");
        botUsersEntity.setCurrentTemperature(40D);
        PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();
        pdfGeneratorService.export(botUsersEntity);
    }


}
