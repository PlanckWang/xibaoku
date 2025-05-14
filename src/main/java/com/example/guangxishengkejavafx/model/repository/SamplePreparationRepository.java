package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SamplePreparationRepository extends JpaRepository<SamplePreparation, Integer> {
    List<SamplePreparation> findBySampleRegistration(SampleRegistration sampleRegistration);
    List<SamplePreparation> findByProduct(Product product);
    List<SamplePreparation> findByStatus(String status);
    List<SamplePreparation> findByBatchNumber(String batchNumber);
    // Add other custom query methods if needed
}

