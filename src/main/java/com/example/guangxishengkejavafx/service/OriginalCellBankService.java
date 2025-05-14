package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.OriginalCellBank;
import com.example.guangxishengkejavafx.model.repository.OriginalCellBankRepository;
import com.example.guangxishengkejavafx.model.repository.SampleRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OriginalCellBankService {

    private final OriginalCellBankRepository originalCellBankRepository;
    private final SampleRegistrationRepository sampleRegistrationRepository; // For linking to samples

    @Autowired
    public OriginalCellBankService(OriginalCellBankRepository originalCellBankRepository, SampleRegistrationRepository sampleRegistrationRepository) {
        this.originalCellBankRepository = originalCellBankRepository;
        this.sampleRegistrationRepository = sampleRegistrationRepository;
    }

    @Transactional(readOnly = true)
    public List<OriginalCellBank> findAll() {
        return originalCellBankRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<OriginalCellBank> findById(Long id) {
        return originalCellBankRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<OriginalCellBank> findByBankId(String bankId) {
        return originalCellBankRepository.findByBankId(bankId);
    }

    @Transactional
    public OriginalCellBank save(OriginalCellBank originalCellBank) {
        // Basic validation or default value setting can be done here
        if (originalCellBank.getEntryDate() == null) {
            originalCellBank.setEntryDate(LocalDate.now());
        }
        if (originalCellBank.getStatus() == null || originalCellBank.getStatus().isEmpty()){
            originalCellBank.setStatus("可用"); // Default status
        }
        // Ensure SampleRegistration is managed if it's new or detached
        if (originalCellBank.getSampleRegistration() != null && originalCellBank.getSampleRegistration().getId() == null) {
            // This assumes SampleRegistration needs to be saved first or is already persisted.
            // For simplicity, we assume sampleRegistration is already a managed entity or has an ID.
            // If SampleRegistration is new, it should be saved by its own service first.
        }
        return originalCellBankRepository.save(originalCellBank);
    }

    @Transactional
    public OriginalCellBank update(Long id, OriginalCellBank updatedOriginalCellBank) {
        return originalCellBankRepository.findById(id)
                .map(existingBank -> {
                    existingBank.setBankId(updatedOriginalCellBank.getBankId());
                    existingBank.setSampleRegistration(updatedOriginalCellBank.getSampleRegistration());
                    existingBank.setCellLineName(updatedOriginalCellBank.getCellLineName());
                    existingBank.setCellType(updatedOriginalCellBank.getCellType());
                    existingBank.setPassageNumber(updatedOriginalCellBank.getPassageNumber());
                    existingBank.setCryopreservationDate(updatedOriginalCellBank.getCryopreservationDate());
                    existingBank.setNumberOfVials(updatedOriginalCellBank.getNumberOfVials());
                    existingBank.setCellsPerVial(updatedOriginalCellBank.getCellsPerVial());
                    existingBank.setVolumePerVial(updatedOriginalCellBank.getVolumePerVial());
                    existingBank.setVolumeUnit(updatedOriginalCellBank.getVolumeUnit());
                    existingBank.setCryopreservationMedium(updatedOriginalCellBank.getCryopreservationMedium());
                    existingBank.setStorageLocation(updatedOriginalCellBank.getStorageLocation());
                    existingBank.setOperator(updatedOriginalCellBank.getOperator());
                    existingBank.setQualityControlStatus(updatedOriginalCellBank.getQualityControlStatus());
                    existingBank.setMycoplasmaTestResult(updatedOriginalCellBank.getMycoplasmaTestResult());
                    existingBank.setSterilityTestResult(updatedOriginalCellBank.getSterilityTestResult());
                    existingBank.setViabilityPostThaw(updatedOriginalCellBank.getViabilityPostThaw());
                    existingBank.setRemarks(updatedOriginalCellBank.getRemarks());
                    existingBank.setStatus(updatedOriginalCellBank.getStatus());
                    existingBank.setEntryDate(updatedOriginalCellBank.getEntryDate());
                    existingBank.setLastModifiedBy(updatedOriginalCellBank.getLastModifiedBy()); // Assuming this is set by the caller or security context
                    return originalCellBankRepository.save(existingBank);
                })
                .orElseThrow(() -> new RuntimeException("OriginalCellBank not found with id: " + id));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!originalCellBankRepository.existsById(id)) {
            throw new RuntimeException("OriginalCellBank not found with id: " + id + " for deletion.");
        }
        originalCellBankRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<OriginalCellBank> findByCellLineName(String cellLineName) {
        return originalCellBankRepository.findByCellLineNameContainingIgnoreCase(cellLineName);
    }

    @Transactional(readOnly = true)
    public List<OriginalCellBank> findByStatus(String status) {
        return originalCellBankRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<OriginalCellBank> findBySampleId(String sampleId) {
        return originalCellBankRepository.findBySampleRegistration_SampleId(sampleId);
    }

    // Add more business-specific methods as needed, e.g.,
    // - updateStatus(Long id, String newStatus)
    // - allocateVials(Long id, int numberOfVialsToAllocate)
    // - generateNextBankId()
}

