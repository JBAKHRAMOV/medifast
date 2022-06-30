package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.constants.ButtonName;
import com.company.dto.admin.AdminDTO;
import com.company.enums.admin.AdminStatus;
import com.company.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

import static com.company.enums.admin.AdminStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final AdminService adminService;

    public void messageController(Message message) {
        var text = "";
        var adminDTO = AdminDTO.getInstance();

        if (message.hasText()) {
            text = message.getText();
        }
        if (Objects.equals(text, "/start"))
            adminService.handleStartMessage(message);
        else if (Objects.equals(adminDTO.getStatus(), BROADCAST_A_MSG)||
                Objects.equals(text, ButtonName.BROADCAST_A_MESSAGE)) {
            adminService.broadcastAMessage(message);
        }

    }
}
