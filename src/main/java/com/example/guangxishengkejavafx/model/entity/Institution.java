package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Institutions") // Ensure this matches your SQLServer table name
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InstitutionID")
    private Integer institutionId;

    @Column(name = "InstitutionName", length = 100, unique = true, nullable = false)
    private String institutionName;

    @Column(name = "Address", length = 255)
    private String address;

    @Column(name = "ContactPerson", length = 50)
    private String contactPerson;

    @Column(name = "ContactPhone", length = 20)
    private String contactPhone;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime updatedAt;

    // Constructors
    public Institution() {
    }

    // Getters and Setters
    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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

