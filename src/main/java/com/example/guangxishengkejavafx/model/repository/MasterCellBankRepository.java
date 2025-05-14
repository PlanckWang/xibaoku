package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.MasterCellBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterCellBankRepository extends JpaRepository<MasterCellBank, Long> {

    Optional<MasterCellBank> findByBankId(String bankId);

    List<MasterCellBank> findByCellLineNameContainingIgnoreCase(String cellLineName);

    List<MasterCellBank> findByStatus(String status);

    List<MasterCellBank> findByOriginalCellBank_BankId(String originalCellBankId);

    List<MasterCellBank> findBySamplePreparation_PreparationId(String samplePreparationId);

    List<MasterCellBank> findBySampleRegistration_SampleId(String sampleId);

}

