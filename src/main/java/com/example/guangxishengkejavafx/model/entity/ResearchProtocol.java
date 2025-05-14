package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ResearchProtocols")
public class ResearchProtocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProtocolID")
    private Integer protocolId;

    @Column(name = "ProtocolTitle", length = 255, nullable = false)
    private String protocolTitle;

    @Column(name = "ProtocolCode", length = 50, unique = true)
    private String protocolCode;

    @Column(name = "ProjectID") // Foreign key to Projects table
    private Integer projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectID", referencedColumnName = "ProjectID", insertable = false, updatable = false)
    private Project project;

    @Column(name = "Version", length = 20)
    private String version;

    @Column(name = "Status", length = 50) // e.g., "草稿", "待审批", "已批准", "执行中", "已完成", "已终止"
    private String status;

    @Column(name = "ApprovalDate")
    private LocalDate approvalDate;

    @Column(name = "EffectiveDate")
    private LocalDate effectiveDate;

    @Column(name = "ExpiryDate")
    private LocalDate expiryDate;

    @Column(name = "PrincipalInvestigatorID") // User ID of the principal investigator
    private Integer principalInvestigatorId;

    @Column(name = "Sponsor", length = 255)
    private String sponsor;

    @Column(name = "Objectives", columnDefinition = "NTEXT")
    private String objectives;

    @Column(name = "Methodology", columnDefinition = "NTEXT")
    private String methodology;

    @Column(name = "InclusionCriteria", columnDefinition = "NTEXT")
    private String inclusionCriteria;

    @Column(name = "ExclusionCriteria", columnDefinition = "NTEXT")
    private String exclusionCriteria;

    @Column(name = "EthicalConsiderations", columnDefinition = "NTEXT")
    private String ethicalConsiderations;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedBy")
    private Integer updatedBy;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Integer protocolId) {
        this.protocolId = protocolId;
    }

    public String getProtocolTitle() {
        return protocolTitle;
    }

    public void setProtocolTitle(String protocolTitle) {
        this.protocolTitle = protocolTitle;
    }

    public String getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(String protocolCode) {
        this.protocolCode = protocolCode;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getPrincipalInvestigatorId() {
        return principalInvestigatorId;
    }

    public void setPrincipalInvestigatorId(Integer principalInvestigatorId) {
        this.principalInvestigatorId = principalInvestigatorId;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getInclusionCriteria() {
        return inclusionCriteria;
    }

    public void setInclusionCriteria(String inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria;
    }

    public String getExclusionCriteria() {
        return exclusionCriteria;
    }

    public void setExclusionCriteria(String exclusionCriteria) {
        this.exclusionCriteria = exclusionCriteria;
    }

    public String getEthicalConsiderations() {
        return ethicalConsiderations;
    }

    public void setEthicalConsiderations(String ethicalConsiderations) {
        this.ethicalConsiderations = ethicalConsiderations;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

