package com.company.api.service;

import com.company.api.dto.PatientDTO;
import com.company.api.entity.PatientEntity;
import com.company.api.enums.PatientStatus;
import com.company.api.error.ItemNotFoundException;
import com.company.api.repo.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class PatientService {
    private final PatientRepository patientRepository;
    private final ModelMapper mapper;

    public void savePatient(PatientEntity entity) {
        patientRepository.save(entity);
    }

    public PageImpl<PatientDTO> getPagination(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdDate");

        var pagination = patientRepository.findAll(pageable);

        var list = pagination
                .stream()
                .map(this::toDTO)
                .toList();
        return new PageImpl<>(list, pageable, pagination.getTotalElements());
    }

    public String updateStatus(PatientStatus status, Long patientId) {
        var entity = patientRepository.findById(patientId).orElseThrow(
                () -> new ItemNotFoundException(String.format("%s id patient not found", patientId))
        );
        entity.setStatus(status);
        patientRepository.save(entity);
        return "Status updated successfully";
    }

    public String deleteById(Long id) {

        var entity = patientRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException(String.format("%s id patient not found", id))
        );

        patientRepository.delete(entity);

        return "deleted successfully";
    }

    public PatientDTO getPatientById(Long id) {
        var entity = patientRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(String.format("%s id patient not found", id)));
        return toDTO(entity);
    }


    private PatientDTO toDTO(PatientEntity entity) {
        return mapper.map(entity, PatientDTO.class);
    }
}
