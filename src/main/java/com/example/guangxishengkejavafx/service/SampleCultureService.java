package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.SampleCulture;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.model.repository.PersonnelRepository;
import com.example.guangxishengkejavafx.model.repository.SampleCultureRepository;
import com.example.guangxishengkejavafx.model.repository.SamplePreparationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SampleCultureService {

    private final SampleCultureRepository sampleCultureRepository;
    private final SamplePreparationRepository samplePreparationRepository;
    private final PersonnelRepository personnelRepository;

    @Autowired
    public SampleCultureService(SampleCultureRepository sampleCultureRepository,
                                SamplePreparationRepository samplePreparationRepository,
                                PersonnelRepository personnelRepository) {
        this.sampleCultureRepository = sampleCultureRepository;
        this.samplePreparationRepository = samplePreparationRepository;
        this.personnelRepository = personnelRepository;
    }

    @Transactional(readOnly = true)
    public List<SampleCulture> findAllSampleCultures() {
        return sampleCultureRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SampleCulture> findSampleCultureById(Integer id) {
        return sampleCultureRepository.findById(id);
    }

    @Transactional
    public SampleCulture saveSampleCulture(SampleCulture sampleCulture) {
        // Validate SamplePreparation
        SamplePreparation preparation = samplePreparationRepository.findById(sampleCulture.getSamplePreparation().getPreparationId().intValue())
            .orElseThrow(() -> new RuntimeException("SamplePreparation not found with ID: " + sampleCulture.getSamplePreparation().getPreparationId()));
        sampleCulture.setSamplePreparation(preparation);

        // Validate Operator (Personnel)
        if (sampleCulture.getOperator() != null && sampleCulture.getOperator().getPersonnelId() != null) {
            Personnel operator = personnelRepository.findById(sampleCulture.getOperator().getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Operator (Personnel) not found with ID: " + sampleCulture.getOperator().getPersonnelId()));
            sampleCulture.setOperator(operator);
        } else {
            sampleCulture.setOperator(null); // Allow null operator if business logic permits
        }

        return sampleCultureRepository.save(sampleCulture);
    }

    @Transactional
    public void deleteSampleCultureById(Integer id) {
        if (!sampleCultureRepository.existsById(id)) {
            throw new RuntimeException("SampleCulture not found with ID: " + id);
        }
        sampleCultureRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SampleCulture> findBySamplePreparation(SamplePreparation samplePreparation) {
        return sampleCultureRepository.findBySamplePreparation(samplePreparation);
    }

    @Transactional(readOnly = true)
    public List<SampleCulture> findByStatus(String status) {
        return sampleCultureRepository.findByStatus(status);
    }

    // Add other business-specific methods as needed
}

