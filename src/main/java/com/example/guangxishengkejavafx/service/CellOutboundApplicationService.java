package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.CellBank;
import com.example.guangxishengkejavafx.model.entity.CellOutboundApplication;
import com.example.guangxishengkejavafx.model.repository.CellBankRepository;
import com.example.guangxishengkejavafx.model.repository.CellOutboundApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CellOutboundApplicationService {

    private final CellOutboundApplicationRepository cellOutboundApplicationRepository;
    private final CellBankRepository cellBankRepository; // To update CellBank status

    @Autowired
    public CellOutboundApplicationService(CellOutboundApplicationRepository cellOutboundApplicationRepository, CellBankRepository cellBankRepository) {
        this.cellOutboundApplicationRepository = cellOutboundApplicationRepository;
        this.cellBankRepository = cellBankRepository;
    }

    public List<CellOutboundApplication> findAll() {
        return cellOutboundApplicationRepository.findAll();
    }

    public Optional<CellOutboundApplication> findById(Integer id) {
        return cellOutboundApplicationRepository.findById(id);
    }

    @Transactional
    public CellOutboundApplication save(CellOutboundApplication application) {
        if (application.getApplicationDate() == null) {
            application.setApplicationDate(LocalDateTime.now());
        }
        if (application.getStatus() == null || application.getStatus().isEmpty()) {
            application.setStatus("待审批"); // Default status for new applications
        }

        // Handle approval logic
        if ("已批准".equals(application.getStatus()) && application.getApprovalDate() == null && application.getApproverId() != null) {
            application.setApprovalDate(LocalDateTime.now());
            // Further logic for when an application is approved but not yet outbound
            // For example, the cell bank might be reserved but not yet marked as "已出库"
        }

        // Handle outbound logic
        if ("已出库".equals(application.getStatus()) && application.getOutboundDate() == null && application.getOutboundHandlerId() != null) {
            application.setOutboundDate(LocalDateTime.now());
            // Update CellBank status to "已出库" or decrement quantity
            Optional<CellBank> cellBankOptional = cellBankRepository.findById(application.getCellBankId());
            if (cellBankOptional.isPresent()) {
                CellBank cellBank = cellBankOptional.get();
                // Assuming a simple status update for now. Quantity management would be more complex.
                cellBank.setStatus("已出库"); 
                cellBankRepository.save(cellBank);
            } else {
                System.err.println("Error: CellBank not found for ID: " + application.getCellBankId() + " during outbound processing.");
                // Optionally throw an exception
            }
        } else if (("已驳回".equals(application.getStatus())) && application.getApprovalDate() == null && application.getApproverId() != null) {
            application.setApprovalDate(LocalDateTime.now());
        }

        return cellOutboundApplicationRepository.save(application);
    }

    public void deleteById(Integer id) {
        // Consider implications: should deleting an application revert CellBank status if already outbound?
        // For now, just delete the application.
        cellOutboundApplicationRepository.deleteById(id);
    }

    public List<CellOutboundApplication> findByProductionPlanId(Integer productionPlanId) {
        return cellOutboundApplicationRepository.findByProductionPlanId(productionPlanId);
    }

    public List<CellOutboundApplication> findByCellBankId(Integer cellBankId) {
        return cellOutboundApplicationRepository.findByCellBankId(cellBankId);
    }

    public List<CellOutboundApplication> findByApplicantId(Integer applicantId) {
        return cellOutboundApplicationRepository.findByApplicantId(applicantId);
    }

    public List<CellOutboundApplication> findByStatus(String status) {
        return cellOutboundApplicationRepository.findByStatus(status);
    }

    public List<CellOutboundApplication> findByApproverId(Integer approverId) {
        return cellOutboundApplicationRepository.findByApproverId(approverId);
    }

    public List<CellOutboundApplication> findByOutboundHandlerId(Integer outboundHandlerId) {
        return cellOutboundApplicationRepository.findByOutboundHandlerId(outboundHandlerId);
    }
}

