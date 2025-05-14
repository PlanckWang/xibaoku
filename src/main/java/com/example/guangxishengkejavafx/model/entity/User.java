package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users") // Ensure this matches your SQLServer table name if it's case sensitive
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "Username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "PasswordHash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "FullName", length = 100)
    private String fullName;

    @Column(name = "Email", length = 100, unique = true)
    private String email;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Column(name = "InstitutionID")
    private Integer institutionId; // Assuming direct mapping for now, can be @ManyToOne later

    @Column(name = "DepartmentID")
    private Integer departmentId; // Assuming direct mapping for now, can be @ManyToOne later

    @Column(name = "Role", length = 50, nullable = false)
    private String role;

    @Column(name = "IsActive", columnDefinition = "BIT DEFAULT 1")
    private Boolean isActive = true;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime updatedAt;

    // Constructors
    public User() {
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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

