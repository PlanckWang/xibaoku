package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Departments") // Ensure this matches your SQLServer table name
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DepartmentID")
    private Integer departmentId;

    @Column(name = "DepartmentName", length = 100, nullable = false)
    private String departmentName;

    @Column(name = "InstitutionID", nullable = false)
    private Integer institutionId; // Foreign key to Institutions table
    // In a more complete setup, this would be: 
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "InstitutionID", nullable = false)
    // private Institution institution;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime updatedAt;

    // Constructors
    public Department() {
    }

    // Getters and Setters
    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

