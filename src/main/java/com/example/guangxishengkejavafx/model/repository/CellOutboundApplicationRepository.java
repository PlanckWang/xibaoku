package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.CellOutboundApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellOutboundApplicationRepository extends JpaRepository<CellOutboundApplication, Integer> {
    List<CellOutboundApplication> findByProductionPlanId(Integer productionPlanId);
    List<CellOutboundApplication> findByCellBankId(Integer cellBankId);
    List<CellOutboundApplication> findByApplicantId(Integer applicantId);
    List<CellOutboundApplication> findByStatus(String status);
    List<CellOutboundApplication> findByApproverId(Integer approverId);
    List<CellOutboundApplication> findByOutboundHandlerId(Integer outboundHandlerId);
}

