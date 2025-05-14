package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Material;
import com.example.guangxishengkejavafx.model.entity.MaterialReturnRecord;
import com.example.guangxishengkejavafx.model.repository.MaterialRepository;
import com.example.guangxishengkejavafx.model.repository.MaterialReturnRecordRepository;
import com.example.guangxishengkejavafx.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialReturnRecordService {

    private final MaterialReturnRecordRepository materialReturnRecordRepository;
    private final MaterialRepository materialRepository; // To update stock and validate material
    private final UserRepository userRepository; // To validate operator user

    @Autowired
    public MaterialReturnRecordService(MaterialReturnRecordRepository materialReturnRecordRepository,
                                       MaterialRepository materialRepository,
                                       UserRepository userRepository) {
        this.materialReturnRecordRepository = materialReturnRecordRepository;
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MaterialReturnRecord> getAllReturnRecords() {
        return materialReturnRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MaterialReturnRecord> getReturnRecordById(Integer id) {
        return materialReturnRecordRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MaterialReturnRecord> getReturnRecordsByMaterialId(Integer materialId) {
        return materialReturnRecordRepository.findByMaterialId(materialId);
    }

    @Transactional
    public MaterialReturnRecord saveReturnRecord(MaterialReturnRecord returnRecord) {
        // 1. Validate Material
        Material material = materialRepository.findById(returnRecord.getMaterialId())
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + returnRecord.getMaterialId()));

        // 2. Validate Operator User if provided
        if (returnRecord.getOperatorUserId() != null && !userRepository.existsById(returnRecord.getOperatorUserId())) {
            throw new RuntimeException("Operator user not found with id: " + returnRecord.getOperatorUserId());
        }

        // 3. Set return date if not already set
        if (returnRecord.getReturnDate() == null) {
            returnRecord.setReturnDate(LocalDateTime.now());
        }

        // 4. Check if there is enough stock to return
        int currentStock = material.getStockQuantity() != null ? material.getStockQuantity() : 0;
        if (currentStock < returnRecord.getReturnQuantity()) {
            throw new RuntimeException("Insufficient stock for material " + material.getMaterialName() + ". Available: " + currentStock + ", Requested: " + returnRecord.getReturnQuantity());
        }

        // 5. Save the return record (this will also calculate total amount via @PrePersist)
        MaterialReturnRecord savedRecord = materialReturnRecordRepository.save(returnRecord);

        // 6. Update material stock quantity (decrease stock)
        material.setStockQuantity(currentStock - returnRecord.getReturnQuantity());
        materialRepository.save(material);

        return savedRecord;
    }

    @Transactional
    public void deleteReturnRecord(Integer recordId) {
        MaterialReturnRecord record = materialReturnRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Return record not found with id: " + recordId));
        
        // Before deleting, consider the impact on stock. 
        // This would mean adding the stock back. This is a critical business logic decision.
        Material material = materialRepository.findById(record.getMaterialId())
            .orElseThrow(() -> new RuntimeException("Associated material not found with id: " + record.getMaterialId()));
        
        int currentStock = material.getStockQuantity() != null ? material.getStockQuantity() : 0;
        int quantityToRevert = record.getReturnQuantity() != null ? record.getReturnQuantity() : 0;
        material.setStockQuantity(currentStock + quantityToRevert); // Add stock back
        materialRepository.save(material);
        
        materialReturnRecordRepository.deleteById(recordId);
    }
    
    // Update for return records might also be restricted or require creating adjustment records.
    // For now, an update method is not provided, similar to inbound records.
}

