package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patents")
public class Patent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PatentID")
    private Long patentId;

    @Column(name = "PatentName", nullable = false, length = 255)
    private String patentName;

    @Column(name = "PatentNumber", nullable = false, unique = true, length = 100)
    private String patentNumber;

    @Lob
    @Column(name = "Applicants")
    private String applicants; // Can be multiple, comma-separated

    @Lob
    @Column(name = "Inventors")
    private String inventors; // Can be multiple, comma-separated

    @Column(name = "ApplicationDate")
    private LocalDate applicationDate;

    @Column(name = "AuthorizationDate")
    private LocalDate authorizationDate;

    @Column(name = "PatentType", length = 50)
    private String patentType; // e.g., Invention, Utility Model, Design

    @Column(name = "LegalStatus", length = 50)
    private String legalStatus; // e.g., Applied, Granted, Expired, Abandoned

    @Lob
    @Column(name = "Abstract")
    private String abstractContent; // Changed from 'Abstract' to avoid keyword conflict

    @Lob
    @Column(name = "Keywords")
    private String keywords; // Comma-separated

    @Column(name = "ProjectID")
    private Long projectId;

    @Column(name = "AttachmentPath", length = 255)
    private String attachmentPath;

    @Lob
    @Column(name = "Remarks")
    private String remarks;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Getters and Setters

    public Long getPatentId() {
        return patentId;
    }

    public void setPatentId(Long patentId) {
        this.patentId = patentId;
    }

    public String getPatentName() {
        return patentName;
    }

    public void setPatentName(String patentName) {
        this.patentName = patentName;
    }

    public String getPatentNumber() {
        return patentNumber;
    }

    public void setPatentNumber(String patentNumber) {
        this.patentNumber = patentNumber;
    }

    public String getApplicants() {
        return applicants;
    }

    public void setApplicants(String applicants) {
        this.applicants = applicants;
    }

    public String getInventors() {
        return inventors;
    }

    public void setInventors(String inventors) {
        this.inventors = inventors;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public LocalDate getAuthorizationDate() {
        return authorizationDate;
    }

    public void setAuthorizationDate(LocalDate authorizationDate) {
        this.authorizationDate = authorizationDate;
    }

    public String getPatentType() {
        return patentType;
    }

    public void setPatentType(String patentType) {
        this.patentType = patentType;
    }

    public String getLegalStatus() {
        return legalStatus;
    }

    public void setLegalStatus(String legalStatus) {
        this.legalStatus = legalStatus;
    }

    public String getAbstractContent() {
        return abstractContent;
    }

    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

