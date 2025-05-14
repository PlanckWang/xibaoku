package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Patent;
import com.example.guangxishengkejavafx.model.repository.PatentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatentService {

    private final PatentRepository patentRepository;

    @Autowired
    public PatentService(PatentRepository patentRepository) {
        this.patentRepository = patentRepository;
    }

    @Transactional(readOnly = true)
    public List<Patent> findAllPatents() {
        return patentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Patent> findPatentById(Long id) {
        return patentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Patent findByPatentNumber(String patentNumber) {
        return patentRepository.findByPatentNumber(patentNumber);
    }

    @Transactional
    public Patent savePatent(Patent patent) {
        // Add any business logic before saving, e.g., validation
        if (patent.getPatentNumber() != null && patentRepository.findByPatentNumber(patent.getPatentNumber()) != null && (patent.getPatentId() == null || !patentRepository.findByPatentNumber(patent.getPatentNumber()).getPatentId().equals(patent.getPatentId()))) {
            throw new IllegalArgumentException("Patent number already exists: " + patent.getPatentNumber());
        }
        return patentRepository.save(patent);
    }

    @Transactional
    public void deletePatent(Long id) {
        patentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Patent> searchPatentsByName(String name) {
        return patentRepository.findByPatentNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Patent> findByPatentType(String patentType) {
        return patentRepository.findByPatentType(patentType);
    }

    @Transactional(readOnly = true)
    public List<Patent> findByLegalStatus(String legalStatus) {
        return patentRepository.findByLegalStatus(legalStatus);
    }

    @Transactional(readOnly = true)
    public List<Patent> findByProjectId(Long projectId) {
        return patentRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Patent> searchPatentsByKeyword(String keyword) {
        return patentRepository.findByKeywordsContainingIgnoreCase(keyword);
    }

    // Add more service methods as needed to support UI interactions and business rules
}

