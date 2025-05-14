package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "AssessmentReports")
public class AssessmentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID")
    private Integer reportId;

    @Column(name = "ReportTitle", length = 255, nullable = false)
    private String reportTitle;

    @Column(name = "ProtocolID") // Foreign key to ResearchProtocols table
    private Integer protocolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProtocolID", referencedColumnName = "ProtocolID", insertable = false, updatable = false)
    private ResearchProtocol researchProtocol;

    @Column(name = "PatientID") // Foreign key to Patients/Depositors table (assuming Depositor is Patient)
    private Integer patientId;

    // Assuming a Depositor entity exists and can be linked as Patient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientID", referencedColumnName = "DepositorID", insertable = false, updatable = false)
    private Depositor patient; // Or a dedicated Patient entity if it exists

    @Column(name = "AssessmentDate")
    private LocalDate assessmentDate;

    @Column(name = "AssessorID") // User ID of the assessor
    private Integer assessorId;

    @Column(name = "ReportType", length = 100) // e.g., "中期评估", "最终评估", "安全性评估"
    private String reportType;

    @Column(name = "Summary", columnDefinition = "NTEXT")
    private String summary;

    @Column(name = "Findings", columnDefinition = "NTEXT")
    private String findings;

    @Column(name = "Conclusions", columnDefinition = "NTEXT")
    private String conclusions;

    @Column(name = "Recommendations", columnDefinition = "NTEXT")
    private String recommendations;

    @Column(name = "Status", length = 50) // e.g., "草稿", "待审核", "已审核", "已发布"
    private String status;

    @Column(name = "Version", length = 20)
    private String version;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedBy")
    private Integer updatedBy;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public Integer getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Integer protocolId) {
        this.protocolId = protocolId;
    }

    public ResearchProtocol getResearchProtocol() {
        return researchProtocol;
    }

    public void setResearchProtocol(ResearchProtocol researchProtocol) {
        this.researchProtocol = researchProtocol;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Depositor getPatient() {
        return patient;
    }

    public void setPatient(Depositor patient) {
        this.patient = patient;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public Integer getAssessorId() {
        return assessorId;
    }

    public void setAssessorId(Integer assessorId) {
        this.assessorId = assessorId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getConclusions() {
        return conclusions;
    }

    public void setConclusions(String conclusions) {
        this.conclusions = conclusions;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

