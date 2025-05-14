package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.FrozenSample;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrozenSampleRepository extends JpaRepository<FrozenSample, Integer> {
    List<FrozenSample> findBySamplePreparation(SamplePreparation samplePreparation);
    List<FrozenSample> findByStatus(String status);
    List<FrozenSample> findByStorageLocation(String storageLocation);
    // Add other custom query methods if needed
}

