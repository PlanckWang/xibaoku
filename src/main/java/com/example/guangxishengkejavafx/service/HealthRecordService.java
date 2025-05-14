package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.HealthRecord;
import com.example.guangxishengkejavafx.model.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;

    @Autowired
    public HealthRecordService(HealthRecordRepository healthRecordRepository) {
        this.healthRecordRepository = healthRecordRepository;
    }

    public List<HealthRecord> findAll() {
        return healthRecordRepository.findAll();
    }

    public Optional<HealthRecord> findById(Integer id) {
        return healthRecordRepository.findById(id);
    }

    public List<HealthRecord> findByPatientId(Integer patientId) {
        return healthRecordRepository.findByPatientId(patientId);
    }

    public List<HealthRecord> findByRecordDate(LocalDate recordDate) {
        return healthRecordRepository.findByRecordDate(recordDate);
    }

    public List<HealthRecord> findByRecordTypeContaining(String recordType) {
        return healthRecordRepository.findByRecordTypeContainingIgnoreCase(recordType);
    }

    public List<HealthRecord> findByRecordedBy(Integer recordedBy) {
        return healthRecordRepository.findByRecordedBy(recordedBy);
    }

    public List<HealthRecord> findByPatientIdAndRecordDateBetween(Integer patientId, LocalDate startDate, LocalDate endDate) {
        return healthRecordRepository.findByPatientIdAndRecordDateBetween(patientId, startDate, endDate);
    }

    @Transactional
    public HealthRecord save(HealthRecord healthRecord) {
        // Set timestamps
        if (healthRecord.getRecordId() == null) { // New record
            healthRecord.setCreatedAt(LocalDateTime.now());
            // healthRecord.setRecordedBy(currentUser.getId()); // Assuming currentUser is available and sets recordedBy
        } else { // Existing record
            // Keep original createdAt and recordedBy if not explicitly changed
            healthRecordRepository.findById(healthRecord.getRecordId()).ifPresent(hr -> {
                if (healthRecord.getCreatedAt() == null) healthRecord.setCreatedAt(hr.getCreatedAt());
                if (healthRecord.getRecordedBy() == null) healthRecord.setRecordedBy(hr.getRecordedBy());
            });
        }
        healthRecord.setUpdatedAt(LocalDateTime.now());
        return healthRecordRepository.save(healthRecord);
    }

    @Transactional
    public void deleteById(Integer id) throws Exception {
        if (!healthRecordRepository.existsById(id)) {
            throw new Exception("无法找到ID为 " + id + " 的健康记录进行删除。");
        }
        healthRecordRepository.deleteById(id);
    }

    public List<HealthRecord> findAll(Specification<HealthRecord> spec) {
        return healthRecordRepository.findAll(spec);
    }
}

