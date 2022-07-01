package com.company.controller;

import com.company.config.TelegramBotConfig;
import com.company.constants.ButtonName;
import com.company.dto.admin.AdminDTO;
import com.company.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

import static com.company.enums.admin.AdminStatus.BROADCAST_A_MSG;

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
        else if (Objects.equals(adminDTO.getStatus(), BROADCAST_A_MSG) ||
                Objects.equals(text, ButtonName.BROADCAST_A_MESSAGE)) {
            adminService.broadcastAMessage(message);
        } else {
            sendMessage.setText("/start");
            telegramBotConfig.sendMsg(sendMessage);
        }

    }
}
