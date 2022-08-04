package com.company.api.controller;

import com.company.api.dto.PatientDTO;
import com.company.api.dto.PatientFullResponseDTO;
import com.company.api.entity.PatientEntity;
import com.company.api.enums.PatientStatus;
import com.company.api.service.PatientService;
import com.company.bot.entity.BotUsersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/patient")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientFullResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(patientService.getPagination(page, size));
    }
}
