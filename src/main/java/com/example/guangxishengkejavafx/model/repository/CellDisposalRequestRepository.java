package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.CellDisposalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellDisposalRequestRepository extends JpaRepository<CellDisposalRequest, Integer> {
    List<CellDisposalRequest> findByCellBankId(Integer cellBankId);
    List<CellDisposalRequest> findByRequesterId(Integer requesterId);
    List<CellDisposalRequest> findByStatus(String status);
    List<CellDisposalRequest> findByApproverId(Integer approverId);
}

