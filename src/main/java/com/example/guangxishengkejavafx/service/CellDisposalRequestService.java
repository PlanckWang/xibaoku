package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.CellBank;
import com.example.guangxishengkejavafx.model.entity.CellDisposalRequest;
import com.example.guangxishengkejavafx.model.repository.CellBankRepository;
import com.example.guangxishengkejavafx.model.repository.CellDisposalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CellDisposalRequestService {

    private final CellDisposalRequestRepository cellDisposalRequestRepository;
    private final CellBankRepository cellBankRepository; // To update CellBank status

    @Autowired
    public CellDisposalRequestService(CellDisposalRequestRepository cellDisposalRequestRepository, CellBankRepository cellBankRepository) {
        this.cellDisposalRequestRepository = cellDisposalRequestRepository;
        this.cellBankRepository = cellBankRepository;
    }

    public List<CellDisposalRequest> findAll() {
        return cellDisposalRequestRepository.findAll();
    }

    public Optional<CellDisposalRequest> findById(Integer id) {
        return cellDisposalRequestRepository.findById(id);
    }

    @Transactional
    public CellDisposalRequest save(CellDisposalRequest request) {
        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDateTime.now());
        }
        if (request.getStatus() == null || request.getStatus().isEmpty()) {
            request.setStatus("待审批"); // Default status for new requests
        }

        // If the request is being approved, update CellBank status
        if ("已批准".equals(request.getStatus()) && request.getApprovalDate() == null) {
            request.setApprovalDate(LocalDateTime.now()); // Set approval date if not already set
            Optional<CellBank> cellBankOptional = cellBankRepository.findById(request.getCellBankId());
            if (cellBankOptional.isPresent()) {
                CellBank cellBank = cellBankOptional.get();
                cellBank.setStatus("已废弃"); // Update CellBank status to '已废弃'
                cellBankRepository.save(cellBank);
            } else {
                // Handle case where CellBank is not found
                System.err.println("Error: CellBank not found for ID: " + request.getCellBankId() + " during disposal approval.");
                // Optionally throw an exception or handle as per business rules
            }
        } else if ("已驳回".equals(request.getStatus()) && request.getApprovalDate() == null){
             request.setApprovalDate(LocalDateTime.now()); // Also set approval date for rejection
        }

        return cellDisposalRequestRepository.save(request);
    }

    public void deleteById(Integer id) {
        // Consider implications: should deleting a request revert CellBank status?
        // For now, just delete the request.
        cellDisposalRequestRepository.deleteById(id);
    }

    public List<CellDisposalRequest> findByCellBankId(Integer cellBankId) {
        return cellDisposalRequestRepository.findByCellBankId(cellBankId);
    }

    public List<CellDisposalRequest> findByRequesterId(Integer requesterId) {
        return cellDisposalRequestRepository.findByRequesterId(requesterId);
    }

    public List<CellDisposalRequest> findByStatus(String status) {
        return cellDisposalRequestRepository.findByStatus(status);
    }

    public List<CellDisposalRequest> findByApproverId(Integer approverId) {
        return cellDisposalRequestRepository.findByApproverId(approverId);
    }
}

