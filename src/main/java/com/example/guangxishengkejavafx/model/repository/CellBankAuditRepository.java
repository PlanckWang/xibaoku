package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.CellBankAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellBankAuditRepository extends JpaRepository<CellBankAudit, Integer> {
    List<CellBankAudit> findByCellBankId(Integer cellBankId);
    List<CellBankAudit> findByAuditorId(Integer auditorId);
    List<CellBankAudit> findByAuditStatus(String auditStatus);
}

