package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.SampleInspectionRequest;
import com.example.guangxishengkejavafx.model.entity.SampleTestResult;
import com.example.guangxishengkejavafx.model.repository.PersonnelRepository;
import com.example.guangxishengkejavafx.model.repository.SampleInspectionRequestRepository;
import com.example.guangxishengkejavafx.model.repository.SampleTestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SampleTestResultService {

    private final SampleTestResultRepository sampleTestResultRepository;
    private final SampleInspectionRequestRepository sampleInspectionRequestRepository;
    private final PersonnelRepository personnelRepository;

    @Autowired
    public SampleTestResultService(SampleTestResultRepository sampleTestResultRepository,
                                   SampleInspectionRequestRepository sampleInspectionRequestRepository,
                                   PersonnelRepository personnelRepository) {
        this.sampleTestResultRepository = sampleTestResultRepository;
        this.sampleInspectionRequestRepository = sampleInspectionRequestRepository;
        this.personnelRepository = personnelRepository;
    }

    @Transactional(readOnly = true)
    public List<SampleTestResult> findAllSampleTestResults() {
        return sampleTestResultRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SampleTestResult> findSampleTestResultById(Integer id) {
        return sampleTestResultRepository.findById(id);
    }

    @Transactional
    public SampleTestResult saveSampleTestResult(SampleTestResult sampleTestResult) {
        // Validate SampleInspectionRequest
        SampleInspectionRequest request = sampleInspectionRequestRepository.findById(sampleTestResult.getSampleInspectionRequest().getRequestId())
            .orElseThrow(() -> new RuntimeException("SampleInspectionRequest not found with ID: " + sampleTestResult.getSampleInspectionRequest().getRequestId()));
        sampleTestResult.setSampleInspectionRequest(request);

        // Validate Tester (Personnel)
        if (sampleTestResult.getTester() != null && sampleTestResult.getTester().getPersonnelId() != null) {
            Personnel tester = personnelRepository.findById(sampleTestResult.getTester().getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Tester (Personnel) not found with ID: " + sampleTestResult.getTester().getPersonnelId()));
            sampleTestResult.setTester(tester);
        } else {
            sampleTestResult.setTester(null); // Allow null tester if business logic permits
        }
        
        // Check if a result already exists for this request if it's a new result
        if (sampleTestResult.getResultId() == null) {
            sampleTestResultRepository.findBySampleInspectionRequest(request).ifPresent(existingResult -> {
                throw new RuntimeException("A test result already exists for request ID: " + request.getRequestId());
            });
        }

        return sampleTestResultRepository.save(sampleTestResult);
    }

    @Transactional
    public void deleteSampleTestResultById(Integer id) {
        if (!sampleTestResultRepository.existsById(id)) {
            throw new RuntimeException("SampleTestResult not found with ID: " + id);
        }
        sampleTestResultRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<SampleTestResult> findBySampleInspectionRequest(SampleInspectionRequest sampleInspectionRequest) {
        return sampleTestResultRepository.findBySampleInspectionRequest(sampleInspectionRequest);
    }

    @Transactional(readOnly = true)
    public List<SampleTestResult> findByStatus(String status) {
        return sampleTestResultRepository.findByStatus(status);
    }

    // Add other business-specific methods as needed
}

