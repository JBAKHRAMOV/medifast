package com.company;

import com.company.api.enums.PatientStatus;
import com.company.api.repo.PatientRepository;
import com.company.api.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SpringBootTest
class MediFastApplicationTests {


    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;
    @Test
    void contextLoads() {
        patientService.filterByStatus(PatientStatus.PENDING, 0,100);

        Sort sort = Sort.by(Sort.Direction.DESC, "created_date");

        Pageable pageable = PageRequest.of(0, 10, sort);
        patientRepository.searchPatient("dsa%",pageable);
    }




}
