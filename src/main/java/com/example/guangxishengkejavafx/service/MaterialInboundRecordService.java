package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Material;
import com.example.guangxishengkejavafx.model.entity.MaterialInboundRecord;
import com.example.guangxishengkejavafx.model.repository.MaterialInboundRecordRepository;
import com.example.guangxishengkejavafx.model.repository.MaterialRepository;
import com.example.guangxishengkejavafx.model.repository.UserRepository; // For operator user validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialInboundRecordService {

    private final MaterialInboundRecordRepository materialInboundRecordRepository;
    private final MaterialRepository materialRepository; // To update stock and validate material
    private final UserRepository userRepository; // To validate operator user

    @Autowired
    public MaterialInboundRecordService(MaterialInboundRecordRepository materialInboundRecordRepository, 
                                      MaterialRepository materialRepository,
                                      UserRepository userRepository) {
        this.materialInboundRecordRepository = materialInboundRecordRepository;
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MaterialInboundRecord> getAllInboundRecords() {
        return materialInboundRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MaterialInboundRecord> getInboundRecordById(Integer id) {
        return materialInboundRecordRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MaterialInboundRecord> getInboundRecordsByMaterialId(Integer materialId) {
        return materialInboundRecordRepository.findByMaterialId(materialId);
    }

    @Transactional
    public MaterialInboundRecord saveInboundRecord(MaterialInboundRecord inboundRecord) {
        // 1. Validate Material
        Material material = materialRepository.findById(inboundRecord.getMaterialId())
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + inboundRecord.getMaterialId()));

        // 2. Validate Operator User if provided
        if (inboundRecord.getOperatorUserId() != null && !userRepository.existsById(inboundRecord.getOperatorUserId())) {
            throw new RuntimeException("Operator user not found with id: " + inboundRecord.getOperatorUserId());
        }

        // 3. Set inbound date if not already set (though typically it should be)
        if (inboundRecord.getInboundDate() == null) {
            inboundRecord.setInboundDate(LocalDateTime.now());
        }

        // 4. Save the inbound record (this will also calculate total amount via @PrePersist)
        MaterialInboundRecord savedRecord = materialInboundRecordRepository.save(inboundRecord);

        // 5. Update material stock quantity
        int currentStock = material.getStockQuantity() != null ? material.getStockQuantity() : 0;
        material.setStockQuantity(currentStock + inboundRecord.getInboundQuantity());
        materialRepository.save(material);

        return savedRecord;
    }

    // Update and Delete for inbound records are typically restricted or handled with care (e.g., adjustments)
    // For now, let's assume they are not primary operations or require special handling.
    // If updates are needed, they should likely create adjustment records rather than modifying historical data.

    @Transactional
    public void deleteInboundRecord(Integer recordId) {
        MaterialInboundRecord record = materialInboundRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Inbound record not found with id: " + recordId));
        
        // Before deleting, consider the impact on stock. 
        // This might require an adjustment or a reverse transaction logic.
        // For simplicity here, we'll revert the stock change. This is a critical business logic decision.
        Material material = materialRepository.findById(record.getMaterialId())
            .orElseThrow(() -> new RuntimeException("Associated material not found with id: " + record.getMaterialId()));
        
        int currentStock = material.getStockQuantity() != null ? material.getStockQuantity() : 0;
        int quantityToRevert = record.getInboundQuantity() != null ? record.getInboundQuantity() : 0;
        material.setStockQuantity(currentStock - quantityToRevert);
        if (material.getStockQuantity() < 0) {
            // This indicates a potential issue or that stock was consumed/moved after this inbound.
            // Handle as per business rules (e.g., prevent deletion, log warning, set to 0).
            // For now, we'll log a warning and set to 0 if it goes negative from this operation.
            System.err.println("Warning: Reverting inbound record " + recordId + " for material " + material.getMaterialId() + " resulted in negative stock. Setting stock to 0.");
            material.setStockQuantity(0);
        }
        materialRepository.save(material);
        
        materialInboundRecordRepository.deleteById(recordId);
    }
}

