package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "depositors") // "储户管理"
@Data
public class Depositor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "depositor_id")
    private Integer depositorId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "gender", length = 10)
    private String gender; // e.g., Male, Female, Other

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "id_card_number", unique = true, length = 50)
    private String idCardNumber; // 身份证号

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 30)
    private String emergencyContactPhone;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Link to User who registered this depositor, if applicable
    @Column(name = "registered_by_user_id")
    private Integer registeredByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User registeredByUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getCustomerName() {
        return name;
    }
    
    // 手动添加getter和setter方法
    public Integer getCustomerId() {
        return depositorId;
    }
    
    public void setCustomerId(Integer depositorId) {
        this.depositorId = depositorId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

