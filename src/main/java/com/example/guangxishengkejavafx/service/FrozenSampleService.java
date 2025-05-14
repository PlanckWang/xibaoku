package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.FrozenSample;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.repository.FrozenSampleRepository;
import com.example.guangxishengkejavafx.model.repository.SamplePreparationRepository;
import com.example.guangxishengkejavafx.model.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FrozenSampleService {

    private final FrozenSampleRepository frozenSampleRepository;
    private final SamplePreparationRepository samplePreparationRepository;
    private final PersonnelRepository personnelRepository;

    @Autowired
    public FrozenSampleService(FrozenSampleRepository frozenSampleRepository,
                               SamplePreparationRepository samplePreparationRepository,
                               PersonnelRepository personnelRepository) {
        this.frozenSampleRepository = frozenSampleRepository;
        this.samplePreparationRepository = samplePreparationRepository;
        this.personnelRepository = personnelRepository;
    }

    @Transactional(readOnly = true)
    public List<FrozenSample> findAllFrozenSamples() {
        return frozenSampleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<FrozenSample> findFrozenSampleById(Integer id) {
        return frozenSampleRepository.findById(id);
    }

    @Transactional
    public FrozenSample saveFrozenSample(FrozenSample frozenSample) {
        // Validate SamplePreparation
        SamplePreparation preparation = samplePreparationRepository.findById(frozenSample.getSamplePreparation().getPreparationId().intValue())
            .orElseThrow(() -> new RuntimeException("SamplePreparation not found with ID: " + frozenSample.getSamplePreparation().getPreparationId()));
        frozenSample.setSamplePreparation(preparation);

        // Validate Operator (Personnel)
        Personnel operator = personnelRepository.findById(frozenSample.getOperator().getPersonnelId().intValue())
            .orElseThrow(() -> new RuntimeException("Operator (Personnel) not found with ID: " + frozenSample.getOperator().getPersonnelId()));
        frozenSample.setOperator(operator);

        return frozenSampleRepository.save(frozenSample);
    }

    @Transactional
    public void deleteFrozenSampleById(Integer id) {
        if (!frozenSampleRepository.existsById(id)) {
            throw new RuntimeException("FrozenSample not found with ID: " + id);
        }
        frozenSampleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<FrozenSample> findBySamplePreparation(SamplePreparation samplePreparation) {
        return frozenSampleRepository.findBySamplePreparation(samplePreparation);
    }

    @Transactional(readOnly = true)
    public List<FrozenSample> findByStatus(String status) {
        return frozenSampleRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<FrozenSample> findByStorageLocation(String storageLocation) {
        return frozenSampleRepository.findByStorageLocation(storageLocation);
    }

    // Add other business-specific methods as needed
}

