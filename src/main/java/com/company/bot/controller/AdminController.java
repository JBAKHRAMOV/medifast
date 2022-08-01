package com.company.bot.controller;

import com.company.bot.dto.admin.AdminDTO;
import com.company.bot.enums.admin.AdminStatus;
import com.company.bot.service.AdminService;
import com.company.bot.config.TelegramBotConfig;
import com.company.bot.constants.ButtonName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    @Lazy
    private final TelegramBotConfig telegramBotConfig;
    private final AdminService adminService;

    @Value("${user.admin}")
    private Long adminId;

    public void messageController(Message message) {
        var text = "";
        var adminDTO = AdminDTO.getInstance();

        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(adminId));

        if (message.hasText()) {
            text = message.getText();
        }
        if (Objects.equals(text, "/start"))
            adminService.handleStartMessage(message);
        else if (Objects.equals(adminDTO.getStatus(), AdminStatus.BROADCAST_A_MSG) ||
                Objects.equals(text, ButtonName.BROADCAST_A_MESSAGE)) {
            adminService.broadcastAMessage(message);
        } else if (Objects.equals(adminDTO.getStatus(), AdminStatus.STATS) ||
                Objects.equals(text, ButtonName.ADMIN_STATS)) {
            adminService.handleStats();
        } else {
            sendMessage.setText("/start");
            telegramBotConfig.sendMsg(sendMessage);
        }

    }
}
