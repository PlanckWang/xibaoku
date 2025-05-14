package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.Patent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatentRepository extends JpaRepository<Patent, Long> {

    // Find by patent number (assuming it should be unique)
    Patent findByPatentNumber(String patentNumber);

    // Find by patent name containing (for searching)
    List<Patent> findByPatentNameContainingIgnoreCase(String patentName);

    // Find by patent type
    List<Patent> findByPatentType(String patentType);

    // Find by legal status
    List<Patent> findByLegalStatus(String legalStatus);

    // Find by project ID
    List<Patent> findByProjectId(Long projectId);

    // Find by keywords containing
    List<Patent> findByKeywordsContainingIgnoreCase(String keyword);

    // Add more custom query methods as needed based on original website's functionality
}

