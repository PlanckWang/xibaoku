package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.OriginalCellBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OriginalCellBankRepository extends JpaRepository<OriginalCellBank, Long> {

    Optional<OriginalCellBank> findByBankId(String bankId);

    List<OriginalCellBank> findByCellLineNameContainingIgnoreCase(String cellLineName);

    List<OriginalCellBank> findByStatus(String status);

    List<OriginalCellBank> findBySampleRegistration_SampleId(String sampleId);

}

