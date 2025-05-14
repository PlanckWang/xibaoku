package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectID")
    private Integer projectId;

    @Column(name = "ProjectName", length = 255, nullable = false)
    private String projectName;

    @Column(name = "ProjectTypeID", nullable = false)
    private Integer projectTypeId; // Foreign key to ProjectTypes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectTypeID", referencedColumnName = "ProjectTypeID", insertable = false, updatable = false)
    private ProjectType projectType;

    @Column(name = "ProjectCode", length = 50, unique = true)
    private String projectCode;

    @Column(name = "PrincipalInvestigatorID") // Assuming this links to a Personnel or User ID
    private Integer principalInvestigatorId;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "Status", length = 50) // e.g., "进行中", "已完成", "已暂停", "已取消"
    private String status;

    @Column(name = "Budget", precision = 18, scale = 2)
    private Double budget;

    @Column(name = "Description", columnDefinition = "NTEXT")
    private String description;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedBy")
    private Integer updatedBy;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(Integer projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Integer getPrincipalInvestigatorId() {
        return principalInvestigatorId;
    }

    public void setPrincipalInvestigatorId(Integer principalInvestigatorId) {
        this.principalInvestigatorId = principalInvestigatorId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

