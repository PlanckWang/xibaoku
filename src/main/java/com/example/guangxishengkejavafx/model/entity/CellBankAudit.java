package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CellBankAudits")
public class CellBankAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuditID")
    private Integer auditId;

    @Column(name = "CellBankID", nullable = false)
    private Integer cellBankId;

    @Column(name = "AuditorID", nullable = false)
    private Integer auditorId;

    @Column(name = "AuditDate")
    private LocalDateTime auditDate;

    @Column(name = "AuditStatus", nullable = false)
    private String auditStatus;

    @Column(name = "AuditComments", columnDefinition = "NTEXT")
    private String auditComments;

    // Getters and Setters
    public Integer getAuditId() {
        return auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    public Integer getCellBankId() {
        return cellBankId;
    }

    public void setCellBankId(Integer cellBankId) {
        this.cellBankId = cellBankId;
    }

    public Integer getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Integer auditorId) {
        this.auditorId = auditorId;
    }

    public LocalDateTime getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditComments() {
        return auditComments;
    }

    public void setAuditComments(String auditComments) {
        this.auditComments = auditComments;
    }
}

