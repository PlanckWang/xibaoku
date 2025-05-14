package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.CellBank;
import com.example.guangxishengkejavafx.model.entity.CellBankAudit;
import com.example.guangxishengkejavafx.model.repository.CellBankAuditRepository;
import com.example.guangxishengkejavafx.model.repository.CellBankRepository; // Assuming a generic CellBankRepository for all types
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CellBankAuditService {

    private final CellBankAuditRepository cellBankAuditRepository;
    private final CellBankRepository cellBankRepository; // To update CellBank status

    @Autowired
    public CellBankAuditService(CellBankAuditRepository cellBankAuditRepository, CellBankRepository cellBankRepository) {
        this.cellBankAuditRepository = cellBankAuditRepository;
        this.cellBankRepository = cellBankRepository;
    }

    public List<CellBankAudit> findAll() {
        return cellBankAuditRepository.findAll();
    }

    public Optional<CellBankAudit> findById(Integer id) {
        return cellBankAuditRepository.findById(id);
    }

    public List<CellBankAudit> findByCellBankId(Integer cellBankId) {
        return cellBankAuditRepository.findByCellBankId(cellBankId);
    }

    @Transactional
    public CellBankAudit save(CellBankAudit cellBankAudit) {
        cellBankAudit.setAuditDate(LocalDateTime.now()); // Set audit date on save
        CellBankAudit savedAudit = cellBankAuditRepository.save(cellBankAudit);

        // Update the status of the corresponding CellBank entry
        Optional<CellBank> cellBankOptional = cellBankRepository.findById(savedAudit.getCellBankId());
        if (cellBankOptional.isPresent()) {
            CellBank cellBank = cellBankOptional.get();
            if ("通过".equals(savedAudit.getAuditStatus())) {
                cellBank.setStatus("已入库"); // Or other appropriate status based on audit
            } else if ("驳回".equals(savedAudit.getAuditStatus())) {
                cellBank.setStatus("审核驳回"); // Or other appropriate status
            }
            // For other audit statuses, you might set different CellBank statuses
            cellBankRepository.save(cellBank);
        } else {
            // Handle case where CellBank is not found, though this should ideally not happen
            // Log an error or throw an exception
            System.err.println("Error: CellBank not found for ID: " + savedAudit.getCellBankId() + " during audit save.");
        }
        return savedAudit;
    }

    public void deleteById(Integer id) {
        // Consider if deleting an audit record should revert CellBank status or have other implications
        cellBankAuditRepository.deleteById(id);
    }

    // Add other business logic methods as needed, for example:
    // public List<CellBankAudit> findPendingAudits() {
    //     return cellBankAuditRepository.findByAuditStatus("待审核");
    // }
}

