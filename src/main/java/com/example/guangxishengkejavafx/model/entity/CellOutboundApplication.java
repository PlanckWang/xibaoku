package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CellOutboundApplications")
public class CellOutboundApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ApplicationID")
    private Integer applicationId;

    @Column(name = "ProductionPlanID") // Assuming this links to a CellProductionPlan
    private Integer productionPlanId;

    @Column(name = "CellBankID", nullable = false)
    private Integer cellBankId;

    @Column(name = "ApplicantID", nullable = false)
    private Integer applicantId;

    @Column(name = "ApplicationDate")
    private LocalDateTime applicationDate;

    @Column(name = "Reason", columnDefinition = "NTEXT")
    private String reason;

    @Column(name = "Quantity", length = 50)
    private String quantity;

    @Column(name = "Destination", length = 255)
    private String destination;

    @Column(name = "Status", length = 20)
    private String status; // e.g., "待审批", "已批准", "已驳回", "已出库"

    @Column(name = "ApproverID")
    private Integer approverId;

    @Column(name = "ApprovalDate")
    private LocalDateTime approvalDate;

    @Column(name = "ApprovalComments", columnDefinition = "NTEXT")
    private String approvalComments;

    @Column(name = "OutboundHandlerID")
    private Integer outboundHandlerId;

    @Column(name = "OutboundDate")
    private LocalDateTime outboundDate;

    // Getters and Setters
    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getProductionPlanId() {
        return productionPlanId;
    }

    public void setProductionPlanId(Integer productionPlanId) {
        this.productionPlanId = productionPlanId;
    }

    public Integer getCellBankId() {
        return cellBankId;
    }

    public void setCellBankId(Integer cellBankId) {
        this.cellBankId = cellBankId;
    }

    public Integer getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Integer applicantId) {
        this.applicantId = applicantId;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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

    public Integer getOutboundHandlerId() {
        return outboundHandlerId;
    }

    public void setOutboundHandlerId(Integer outboundHandlerId) {
        this.outboundHandlerId = outboundHandlerId;
    }

    public LocalDateTime getOutboundDate() {
        return outboundDate;
    }

    public void setOutboundDate(LocalDateTime outboundDate) {
        this.outboundDate = outboundDate;
    }
}

