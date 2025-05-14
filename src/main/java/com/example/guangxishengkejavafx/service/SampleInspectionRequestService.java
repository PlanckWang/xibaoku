package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.SampleInspectionRequest;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.model.repository.PersonnelRepository;
import com.example.guangxishengkejavafx.model.repository.SampleInspectionRequestRepository;
import com.example.guangxishengkejavafx.model.repository.SampleRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SampleInspectionRequestService {

    private final SampleInspectionRequestRepository sampleInspectionRequestRepository;
    private final SampleRegistrationRepository sampleRegistrationRepository;
    private final PersonnelRepository personnelRepository;

    @Autowired
    public SampleInspectionRequestService(SampleInspectionRequestRepository sampleInspectionRequestRepository,
                                          SampleRegistrationRepository sampleRegistrationRepository,
                                          PersonnelRepository personnelRepository) {
        this.sampleInspectionRequestRepository = sampleInspectionRequestRepository;
        this.sampleRegistrationRepository = sampleRegistrationRepository;
        this.personnelRepository = personnelRepository;
    }

    @Transactional(readOnly = true)
    public List<SampleInspectionRequest> findAllSampleInspectionRequests() {
        return sampleInspectionRequestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SampleInspectionRequest> findSampleInspectionRequestById(Integer id) {
        return sampleInspectionRequestRepository.findById(id);
    }

    @Transactional
    public SampleInspectionRequest saveSampleInspectionRequest(SampleInspectionRequest sampleInspectionRequest) {
        // Validate SampleRegistration
        SampleRegistration registration = sampleRegistrationRepository.findById(sampleInspectionRequest.getSampleRegistration().getRegistrationId())
            .orElseThrow(() -> new RuntimeException("SampleRegistration not found with ID: " + sampleInspectionRequest.getSampleRegistration().getRegistrationId()));
        sampleInspectionRequest.setSampleRegistration(registration);

        // Validate Requester (Personnel)
        if (sampleInspectionRequest.getRequester() != null && sampleInspectionRequest.getRequester().getPersonnelId() != null) {
            Personnel requester = personnelRepository.findById(sampleInspectionRequest.getRequester().getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Requester (Personnel) not found with ID: " + sampleInspectionRequest.getRequester().getPersonnelId()));
            sampleInspectionRequest.setRequester(requester);
        } else {
             sampleInspectionRequest.setRequester(null); // Allow null requester if business logic permits
        }

        return sampleInspectionRequestRepository.save(sampleInspectionRequest);
    }

    @Transactional
    public void deleteSampleInspectionRequestById(Integer id) {
        if (!sampleInspectionRequestRepository.existsById(id)) {
            throw new RuntimeException("SampleInspectionRequest not found with ID: " + id);
        }
        sampleInspectionRequestRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SampleInspectionRequest> findBySampleRegistration(SampleRegistration sampleRegistration) {
        return sampleInspectionRequestRepository.findBySampleRegistration(sampleRegistration);
    }

    @Transactional(readOnly = true)
    public List<SampleInspectionRequest> findByStatus(String status) {
        return sampleInspectionRequestRepository.findByStatus(status);
    }

    // Add other business-specific methods as needed
}

