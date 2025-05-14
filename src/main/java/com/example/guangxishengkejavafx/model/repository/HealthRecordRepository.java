package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Integer>, JpaSpecificationExecutor<HealthRecord> {
    List<HealthRecord> findByPatientId(Integer patientId);
    List<HealthRecord> findByRecordDate(LocalDate recordDate);
    List<HealthRecord> findByRecordTypeContainingIgnoreCase(String recordType);
    List<HealthRecord> findByRecordedBy(Integer recordedBy);
    List<HealthRecord> findByPatientIdAndRecordDateBetween(Integer patientId, LocalDate startDate, LocalDate endDate);
}

