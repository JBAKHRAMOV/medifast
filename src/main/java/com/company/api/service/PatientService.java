package com.company.api.service;

import com.company.api.dto.ImageDTO;
import com.company.api.dto.patient.PatientDTO;
import com.company.api.dto.patient.PatientFullResponseDTO;
import com.company.api.dto.patient.UpdateDiagnosisRequestDTO;
import com.company.api.dto.patient.UpdatePatientStatusRequestDTO;
import com.company.api.entity.ImageEntity;
import com.company.api.entity.PatientEntity;
import com.company.api.enums.PatientStatus;
import com.company.api.error.ItemNotFoundException;
import com.company.api.repo.ImageRepository;
import com.company.api.repo.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class PatientService {
    private final PatientRepository patientRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper mapper;

    public PageImpl<PatientDTO> getPagination(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdDate");

        var pagination = patientRepository.findAll(pageable);

        var list = pagination.stream().map(this::toDTO).toList();
        return new PageImpl<>(list, pageable, pagination.getTotalElements());
    }

    public String updateStatus(UpdatePatientStatusRequestDTO dto) {
        var entity = patientRepository.findById(dto.getPatientId()).orElseThrow(() -> new ItemNotFoundException(String.format("%s id patient not found", dto.getPatientId())));
        entity.setStatus(dto.getStatus());
        patientRepository.updateLastModifiedDate(LocalDateTime.now(), dto.getPatientId());
        patientRepository.save(entity);
        return "Status updated successfully";
    }

    public String deleteById(Long id) {

        var entity = patientRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(String.format("%s id patient not found", id)));
        imageRepository.deleteByPatient(entity);
        patientRepository.delete(entity);

        return "deleted successfully";
    }

    public PageImpl<PatientDTO> filterByStatus(PatientStatus status, int page, int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "created_date");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PatientEntity> entityPage = patientRepository.filterByStatus(status.name(), pageable);

        List<PatientDTO> patientDTOS = entityPage.stream().map(this::toDTO).toList();

        return new PageImpl<>(patientDTOS, pageable, entityPage.getTotalElements());
    }

    public PageImpl<PatientDTO> searchPatient(String value, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created_date");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PatientEntity> entityPage = patientRepository.searchPatient(value.toUpperCase() + "%", pageable);

        List<PatientDTO> patientDTOS = entityPage.stream().map(this::toDTO).toList();

        return new PageImpl<>(patientDTOS, pageable, entityPage.getTotalElements());
    }

    public String updateDiagnosis(UpdateDiagnosisRequestDTO dto) {
        var entity = patientRepository.findById(dto.getPatientId()).orElseThrow(() -> new ItemNotFoundException(String.format("%s id patient not found", dto.getPatientId())));
        entity.setDiagnosis(dto.getDiagnosis());
        patientRepository.save(entity);
        patientRepository.updateLastModifiedDate(LocalDateTime.now(), dto.getPatientId());

        return "diagnosis updated successfully!";
    }

    public PatientFullResponseDTO getPatientById(Long id) {

        var entity = patientRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(String.format("%s id patient not found", id)));
        var patient = toDTO(entity);

        var imageList = imageRepository.findAllByPatient(entity);

        var imageDTOS = imageList.stream().map(this::toImageDTO).toList();

        return PatientFullResponseDTO.builder().patient(patient).imageList(imageDTOS).build();
    }


    private PatientDTO toDTO(PatientEntity entity) {
        return mapper.map(entity, PatientDTO.class);
    }

    private ImageDTO toImageDTO(ImageEntity entity) {
        return ImageDTO.builder()
                .link(entity.getLink())
                .id(entity.getId())
                .type(entity.getType())
                .build();
    }
}
