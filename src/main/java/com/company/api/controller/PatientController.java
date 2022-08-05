package com.company.api.controller;

import com.company.api.dto.patient.PatientFullResponseDTO;
import com.company.api.dto.patient.UpdateDiagnosisRequestDTO;
import com.company.api.enums.PatientStatus;
import com.company.api.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/patient")
@RequiredArgsConstructor
@Api(tags = "PatientController")
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Get patient by id", notes = "This method using for get patient by id")
    public ResponseEntity<PatientFullResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Get patient list", notes = "This method using for get patient pagination list")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(patientService.getPagination(page, size));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Search patient", notes = "This method using for searching patient name or phone number")
    public ResponseEntity<?> searchPatient(@RequestParam String value,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(patientService.searchPatient(value, page, size));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Filter by status", notes = "This method using for filter patient by status")
    public ResponseEntity<?> filterByStatus(@RequestParam PatientStatus status,
                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(patientService.filterByStatus(status, page, size));
    }

    @PutMapping("update-status")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Update status", notes = "This method using for update patient status")
    public ResponseEntity<?> updateStatus(@RequestParam PatientStatus status,
                                          @RequestParam Long patientId) {
        return ResponseEntity.ok(patientService.updateStatus(status, patientId));
    }

    @PutMapping("/update-diagnosis")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Update diagnosis", notes = "This method using for update patient diagnosis")
    public ResponseEntity<?> updateDiagnosis(@RequestBody UpdateDiagnosisRequestDTO dto) {
        return ResponseEntity.ok(patientService.updateDiagnosis(dto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Delete patient by id", notes = "this method using for delete patient by id")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(patientService.deleteById(id));
    }
}
