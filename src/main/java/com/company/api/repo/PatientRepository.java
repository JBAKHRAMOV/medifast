package com.company.api.repo;

import com.company.api.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    @Transactional
    @Modifying
    @Query("update PatientEntity set lastModifiedDate=?1 where id=?2")
    void updateLastModifiedDate(LocalDateTime lastModifiedDate, Long id);


    @Transactional
    @Modifying
    @Query("update PatientEntity set complaints=?1 where id=?2")
    void updateComplaints(String complaints, Long id);

    @Query(value = "select * from  patient where  status = ?1 ", nativeQuery = true)
    Page<PatientEntity> filterByStatus(String status, Pageable pageable);


    @Query(value = "select * from  patient where  upper(name) like ?1 or upper(phone) like ?1 or upper(surname) like ?1", nativeQuery = true)
    Page<PatientEntity> searchPatient(String value, Pageable pageable);

    /*@Column
    private String causeOfComplaint;
    @Column
    private String complaintStartedTime;
    @Column
    private String drugsList;
    @Column
    private String cigarette;
    @Column
    private String diseasesList;*/
    @Transactional
    @Modifying
    @Query("update PatientEntity set causeOfComplaint=?1, complaintStartedTime=?2,drugsList=?3, " +
            " cigarette=?4, diseasesList=?5 where id=?6")
    void updateComplaintsInfo(String causeOfComplaint, String complaintStartedTime, String drugsList, String cigarette,
                              String diseasesList, Long id);
}