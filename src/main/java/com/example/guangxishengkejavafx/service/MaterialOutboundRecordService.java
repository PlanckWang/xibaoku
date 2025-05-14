package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Material;
import com.example.guangxishengkejavafx.model.entity.MaterialOutboundRecord;
import com.example.guangxishengkejavafx.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialOutboundRecordService {

    private final MaterialOutboundRecordRepository materialOutboundRecordRepository;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository; // To validate operator user
    private final DepartmentRepository departmentRepository; // To validate recipient department
    private final PersonnelRepository personnelRepository; // To validate recipient personnel
    // private final ProjectRepository projectRepository; // If project validation is needed

    @Autowired
    public MaterialOutboundRecordService(MaterialOutboundRecordRepository materialOutboundRecordRepository,
                                         MaterialRepository materialRepository,
                                         UserRepository userRepository,
                                         DepartmentRepository departmentRepository,
                                         PersonnelRepository personnelRepository) {
        this.materialOutboundRecordRepository = materialOutboundRecordRepository;
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.personnelRepository = personnelRepository;
    }

    @Transactional(readOnly = true)
    public List<MaterialOutboundRecord> getAllOutboundRecords() {
        return materialOutboundRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MaterialOutboundRecord> getOutboundRecordById(Integer id) {
        return materialOutboundRecordRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MaterialOutboundRecord> getOutboundRecordsByMaterialId(Integer materialId) {
        return materialOutboundRecordRepository.findByMaterialId(materialId);
    }

    @Transactional
    public MaterialOutboundRecord saveOutboundRecord(MaterialOutboundRecord outboundRecord) {
        // 1. Validate Material
        Material material = materialRepository.findById(outboundRecord.getMaterialId())
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + outboundRecord.getMaterialId()));

        // 2. Validate Operator User if provided
        if (outboundRecord.getOperatorUserId() != null && !userRepository.existsById(outboundRecord.getOperatorUserId())) {
            throw new RuntimeException("Operator user not found with id: " + outboundRecord.getOperatorUserId());
        }

        // 3. Validate Recipient Department if provided
        if (outboundRecord.getRecipientDepartmentId() != null && !departmentRepository.existsById(outboundRecord.getRecipientDepartmentId())) {
            throw new RuntimeException("Recipient department not found with id: " + outboundRecord.getRecipientDepartmentId());
        }
        
        // 4. Validate Recipient Personnel if provided
        if (outboundRecord.getRecipientPersonnelId() != null && !personnelRepository.existsById(outboundRecord.getRecipientPersonnelId())) {
            throw new RuntimeException("Recipient personnel not found with id: " + outboundRecord.getRecipientPersonnelId());
        }

        // 5. Set outbound date if not already set
        if (outboundRecord.getOutboundDate() == null) {
            outboundRecord.setOutboundDate(LocalDateTime.now());
        }

        // 6. Check if there is enough stock for outbound
        int currentStock = material.getStockQuantity() != null ? material.getStockQuantity() : 0;
        if (currentStock < outboundRecord.getOutboundQuantity()) {
            throw new RuntimeException("Insufficient stock for material " + material.getMaterialName() + ". Available: " + currentStock + ", Requested for outbound: " + outboundRecord.getOutboundQuantity());
        }

        // 7. Save the outbound record (this will also calculate total amount via @PrePersist)
        MaterialOutboundRecord savedRecord = materialOutboundRecordRepository.save(outboundRecord);

        // 8. Update material stock quantity (decrease stock)
        material.setStockQuantity(currentStock - outboundRecord.getOutboundQuantity());
        materialRepository.save(material);

        return savedRecord;
    }

    @Transactional
    public void deleteOutboundRecord(Integer recordId) {
        MaterialOutboundRecord record = materialOutboundRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Outbound record not found with id: " + recordId));
        
        // Before deleting, consider the impact on stock. 
        // This would mean adding the stock back. This is a critical business logic decision.
        Material material = materialRepository.findById(record.getMaterialId())
            .orElseThrow(() -> new RuntimeException("Associated material not found with id: " + record.getMaterialId()));
        
        int currentStock = material.getStockQuantity() != null ? material.getStockQuantity() : 0;
        int quantityToRevert = record.getOutboundQuantity() != null ? record.getOutboundQuantity() : 0;
        material.setStockQuantity(currentStock + quantityToRevert); // Add stock back
        materialRepository.save(material);
        
        materialOutboundRecordRepository.deleteById(recordId);
    }
    
    // Update for outbound records might also be restricted or require creating adjustment records.
}

