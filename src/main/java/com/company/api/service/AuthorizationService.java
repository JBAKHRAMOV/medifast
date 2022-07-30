package com.company.api.service;

import com.company.api.dto.authorization.AuthDTO;
import com.company.api.dto.authorization.AuthResponseDTO;
import com.company.api.error.AppBadRequestException;
import com.company.api.repo.AdminRepository;
import com.company.api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthorizationService {
    private final AdminRepository adminRepository;

    public AuthResponseDTO login(AuthDTO dto) {
        var encoder = new BCryptPasswordEncoder();

        var admin = adminRepository
                .findByUsername(dto.getUsername())
                .orElseThrow(() -> new AppBadRequestException("Login or Password not valid."));

        if (!encoder.matches(dto.getPassword(), admin.getPassword()))
            throw new AppBadRequestException("Login or Password not valid");

        var jwt = JwtUtil.createJwt(admin.getId(), admin.getUsername());

        /*return AuthResponseDTO.builder()
                .id(admin.getId())
                .name(admin.getName())
                .surname(admin.getSurname())
                .email(admin.getEmail())
                .jwt(jwt)
                .build();*/
        return null;
    }
}
