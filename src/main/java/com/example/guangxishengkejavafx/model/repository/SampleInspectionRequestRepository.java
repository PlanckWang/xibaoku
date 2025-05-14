package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.SampleInspectionRequest;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleInspectionRequestRepository extends JpaRepository<SampleInspectionRequest, Integer> {
    List<SampleInspectionRequest> findBySampleRegistration(SampleRegistration sampleRegistration);
    List<SampleInspectionRequest> findByStatus(String status);
    // Add other custom query methods if needed
}

