package com.company.api.service;

import com.company.api.dto.BotInfoDTO;
import com.company.api.dto.patient.UpdatePasswordRequestDTO;
import com.company.api.error.AppBadRequestException;
import com.company.api.error.ItemNotFoundException;
import com.company.api.repo.AdminRepository;
import com.company.bot.repository.BotUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminApiService {
    private final AdminRepository adminRepository;
    private final BotUsersRepository botUsersRepository;

    public String updatePassword(UpdatePasswordRequestDTO dto) {
        var encoder = new BCryptPasswordEncoder();
        var admin = adminRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new ItemNotFoundException("Admin not found!"));
        if (!encoder.matches(dto.getOldPassword(), admin.getPassword()))
            throw new AppBadRequestException("Password is wrong");
        admin.setPassword(encoder.encode(dto.getNewPassword()));
        adminRepository.save(admin);
        return "Password changed successfully!";
    }

    public BotInfoDTO getStats() {
        return BotInfoDTO.builder()
                .joinedToday(botUsersRepository.joinedToday())
                .totalUsers(botUsersRepository.countAllUsers())
                .build();
    }

}
