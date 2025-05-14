package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.DirectProductRelease;
import com.example.guangxishengkejavafx.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectProductReleaseRepository extends JpaRepository<DirectProductRelease, Integer> {
    List<DirectProductRelease> findByProduct(Product product);
    List<DirectProductRelease> findByBatchNumber(String batchNumber);
    // Add other custom query methods if needed
}

