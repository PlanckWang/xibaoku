package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.SampleCulture;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleCultureRepository extends JpaRepository<SampleCulture, Integer> {
    List<SampleCulture> findBySamplePreparation(SamplePreparation samplePreparation);
    List<SampleCulture> findByStatus(String status);
    // Add other custom query methods if needed
}

