package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.MasterCellBank;
import com.example.guangxishengkejavafx.model.entity.OriginalCellBank;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.model.repository.MasterCellBankRepository;
import com.example.guangxishengkejavafx.model.repository.OriginalCellBankRepository;
import com.example.guangxishengkejavafx.model.repository.SamplePreparationRepository;
import com.example.guangxishengkejavafx.model.repository.SampleRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MasterCellBankService {

    private final MasterCellBankRepository masterCellBankRepository;
    private final OriginalCellBankRepository originalCellBankRepository; // For linking
    private final SamplePreparationRepository samplePreparationRepository; // For linking
    private final SampleRegistrationRepository sampleRegistrationRepository; // For linking

    @Autowired
    public MasterCellBankService(MasterCellBankRepository masterCellBankRepository,
                                 OriginalCellBankRepository originalCellBankRepository,
                                 SamplePreparationRepository samplePreparationRepository,
                                 SampleRegistrationRepository sampleRegistrationRepository) {
        this.masterCellBankRepository = masterCellBankRepository;
        this.originalCellBankRepository = originalCellBankRepository;
        this.samplePreparationRepository = samplePreparationRepository;
        this.sampleRegistrationRepository = sampleRegistrationRepository;
    }

    @Transactional(readOnly = true)
    public List<MasterCellBank> findAll() {
        return masterCellBankRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MasterCellBank> findById(Long id) {
        return masterCellBankRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<MasterCellBank> findByBankId(String bankId) {
        return masterCellBankRepository.findByBankId(bankId);
    }

    @Transactional
    public MasterCellBank save(MasterCellBank masterCellBank, String originalCellBankId, String samplePreparationId, String sampleRegistrationId) {
        // Link OriginalCellBank if ID is provided
        if (originalCellBankId != null && !originalCellBankId.trim().isEmpty()) {
            OriginalCellBank ocb = originalCellBankRepository.findByBankId(originalCellBankId.trim())
                    .orElseThrow(() -> new RuntimeException("OriginalCellBank not found with bankId: " + originalCellBankId));
            masterCellBank.setOriginalCellBank(ocb);
        } else {
            masterCellBank.setOriginalCellBank(null); // Explicitly set to null if not provided
        }

        // Link SamplePreparation if ID is provided
        if (samplePreparationId != null && !samplePreparationId.trim().isEmpty()) {
            SamplePreparation sp = samplePreparationRepository.findByPreparationId(samplePreparationId.trim())
                    .orElseThrow(() -> new RuntimeException("SamplePreparation not found with preparationId: " + samplePreparationId));
            masterCellBank.setSamplePreparation(sp);
        } else {
            masterCellBank.setSamplePreparation(null); // Explicitly set to null if not provided
        }

        // Link SampleRegistration (Mandatory)
        if (sampleRegistrationId == null || sampleRegistrationId.trim().isEmpty()){
            throw new RuntimeException("SampleRegistration ID cannot be null or empty for MasterCellBank");
        }
        SampleRegistration sr = sampleRegistrationRepository.findBySampleId(sampleRegistrationId.trim())
                .orElseThrow(() -> new RuntimeException("SampleRegistration not found with sampleId: " + sampleRegistrationId));
        masterCellBank.setSampleRegistration(sr);

        if (masterCellBank.getEntryDate() == null) {
            masterCellBank.setEntryDate(LocalDate.now());
        }
        if (masterCellBank.getStatus() == null || masterCellBank.getStatus().isEmpty()){
            masterCellBank.setStatus("可用"); // Default status
        }

        return masterCellBankRepository.save(masterCellBank);
    }

    @Transactional
    public MasterCellBank update(Long id, MasterCellBank updatedMasterCellBank, String originalCellBankId, String samplePreparationId, String sampleRegistrationId) {
        MasterCellBank existingBank = masterCellBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MasterCellBank not found with id: " + id));

        // Link OriginalCellBank if ID is provided
        if (originalCellBankId != null && !originalCellBankId.trim().isEmpty()) {
            OriginalCellBank ocb = originalCellBankRepository.findByBankId(originalCellBankId.trim())
                    .orElseThrow(() -> new RuntimeException("OriginalCellBank not found with bankId: " + originalCellBankId));
            existingBank.setOriginalCellBank(ocb);
        } else {
            existingBank.setOriginalCellBank(null);
        }

        // Link SamplePreparation if ID is provided
        if (samplePreparationId != null && !samplePreparationId.trim().isEmpty()) {
            SamplePreparation sp = samplePreparationRepository.findByPreparationId(samplePreparationId.trim())
                    .orElseThrow(() -> new RuntimeException("SamplePreparation not found with preparationId: " + samplePreparationId));
            existingBank.setSamplePreparation(sp);
        } else {
            existingBank.setSamplePreparation(null);
        }

        // Link SampleRegistration (Mandatory)
        if (sampleRegistrationId == null || sampleRegistrationId.trim().isEmpty()){
            throw new RuntimeException("SampleRegistration ID cannot be null or empty for MasterCellBank update");
        }
        SampleRegistration sr = sampleRegistrationRepository.findBySampleId(sampleRegistrationId.trim())
                .orElseThrow(() -> new RuntimeException("SampleRegistration not found with sampleId: " + sampleRegistrationId));
        existingBank.setSampleRegistration(sr);

        existingBank.setBankId(updatedMasterCellBank.getBankId());
        existingBank.setCellLineName(updatedMasterCellBank.getCellLineName());
        existingBank.setCellType(updatedMasterCellBank.getCellType());
        existingBank.setPassageNumber(updatedMasterCellBank.getPassageNumber());
        existingBank.setCryopreservationDate(updatedMasterCellBank.getCryopreservationDate());
        existingBank.setNumberOfVials(updatedMasterCellBank.getNumberOfVials());
        existingBank.setCellsPerVial(updatedMasterCellBank.getCellsPerVial());
        existingBank.setVolumePerVial(updatedMasterCellBank.getVolumePerVial());
        existingBank.setVolumeUnit(updatedMasterCellBank.getVolumeUnit());
        existingBank.setCryopreservationMedium(updatedMasterCellBank.getCryopreservationMedium());
        existingBank.setStorageLocation(updatedMasterCellBank.getStorageLocation());
        existingBank.setOperator(updatedMasterCellBank.getOperator());
        existingBank.setQualityControlStatus(updatedMasterCellBank.getQualityControlStatus());
        existingBank.setMycoplasmaTestResult(updatedMasterCellBank.getMycoplasmaTestResult());
        existingBank.setSterilityTestResult(updatedMasterCellBank.getSterilityTestResult());
        existingBank.setViabilityPostThaw(updatedMasterCellBank.getViabilityPostThaw());
        existingBank.setPassageLimit(updatedMasterCellBank.getPassageLimit());
        existingBank.setRemarks(updatedMasterCellBank.getRemarks());
        existingBank.setStatus(updatedMasterCellBank.getStatus());
        existingBank.setEntryDate(updatedMasterCellBank.getEntryDate());
        // lastModifiedBy should be handled by security context or service layer if needed

        return masterCellBankRepository.save(existingBank);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!masterCellBankRepository.existsById(id)) {
            throw new RuntimeException("MasterCellBank not found with id: " + id + " for deletion.");
        }
        masterCellBankRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MasterCellBank> findByCellLineName(String cellLineName) {
        return masterCellBankRepository.findByCellLineNameContainingIgnoreCase(cellLineName);
    }

    @Transactional(readOnly = true)
    public List<MasterCellBank> findByStatus(String status) {
        return masterCellBankRepository.findByStatus(status);
    }

    // Add more business-specific methods as needed
}

