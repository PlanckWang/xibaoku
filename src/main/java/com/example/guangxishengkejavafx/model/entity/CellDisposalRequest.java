package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CellDisposalRequests")
public class CellDisposalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DisposalRequestID")
    private Integer disposalRequestId;

    @Column(name = "CellBankID", nullable = false)
    private Integer cellBankId;

    @Column(name = "RequestDate")
    private LocalDateTime requestDate;

    @Column(name = "RequesterID", nullable = false)
    private Integer requesterId;

    @Column(name = "Reason", length = 255)
    private String reason;

    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "ApproverID")
    private Integer approverId;

    @Column(name = "ApprovalDate")
    private LocalDateTime approvalDate;

    @Column(name = "ApprovalComments", columnDefinition = "NTEXT")
    private String approvalComments;

    // Getters and Setters
    public Integer getDisposalRequestId() {
        return disposalRequestId;
    }

    public void setDisposalRequestId(Integer disposalRequestId) {
        this.disposalRequestId = disposalRequestId;
    }

    public Integer getCellBankId() {
        return cellBankId;
    }

    public void setCellBankId(Integer cellBankId) {
        this.cellBankId = cellBankId;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public Integer getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalComments() {
        return approvalComments;
    }

    public void setApprovalComments(String approvalComments) {
        this.approvalComments = approvalComments;
    }
}

