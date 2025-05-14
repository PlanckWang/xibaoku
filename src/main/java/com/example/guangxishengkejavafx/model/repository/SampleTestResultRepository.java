package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.SampleInspectionRequest;
import com.example.guangxishengkejavafx.model.entity.SampleTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SampleTestResultRepository extends JpaRepository<SampleTestResult, Integer> {
    Optional<SampleTestResult> findBySampleInspectionRequest(SampleInspectionRequest sampleInspectionRequest);
    List<SampleTestResult> findByStatus(String status);
    // Add other custom query methods if needed
}

