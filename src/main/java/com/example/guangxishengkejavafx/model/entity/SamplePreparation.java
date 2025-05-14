package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal; // For quantity if it can be fractional, or use Integer/Long

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SamplePreparations")
public class SamplePreparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SamplePreparationID")
    private Long samplePreparationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SampleRegistrationID")
    private SampleRegistration sampleRegistration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductID") // Added for linking to Product
    private Product product;

    @Column(name = "BatchNumber", length = 100)
    private String batchNumber;

    @Column(name = "QuantityPrepared")
    private BigDecimal quantityPrepared; // Or Integer/Long if always whole numbers

    @Column(name = "Unit", length = 50)
    private String unit;

    @Column(name = "Status", length = 50)
    private String status;

    @Lob
    @Column(name = "ProcessDetails", columnDefinition = "NTEXT")
    private String processDetails;

    @Lob
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @Column(name = "PreparationDate")
    private LocalDateTime preparationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PreparedByUserID")
    private Personnel operator;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (preparationDate == null) {
            preparationDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getPreparationId() {
        return samplePreparationId;
    }

    public Personnel getPreparedBy() { // Added based on compilation error
        return operator;
    }

    // 手动添加getter和setter方法
    public SampleRegistration getSampleRegistration() {
        return sampleRegistration;
    }

    public void setSampleRegistration(SampleRegistration sampleRegistration) {
        this.sampleRegistration = sampleRegistration;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Personnel getOperator() {
        return operator;
    }

    public void setOperator(Personnel operator) {
        this.operator = operator;
    }

    // Lombok @Data will generate getters and setters for all fields including:
    // getProduct(), setProduct(), getBatchNumber(), setBatchNumber(), 
    // getQuantityPrepared(), setQuantityPrepared(), getUnit(), setUnit(),
    // getStatus(), setStatus(), getProcessDetails(), setProcessDetails(),
    // getNotes(), setNotes()
}

