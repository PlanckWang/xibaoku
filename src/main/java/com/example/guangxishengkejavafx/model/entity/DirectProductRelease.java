package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "DirectProductReleases")
public class DirectProductRelease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReleaseID")
    private Integer releaseId;

    @ManyToOne
    @JoinColumn(name = "ProductID", nullable = false)
    private Product product;

    @Column(name = "BatchNumber", length = 100)
    private String batchNumber;

    @Column(name = "QuantityReleased", nullable = false)
    private Double quantityReleased;

    @Column(name = "Unit", length = 50, nullable = false)
    private String unit;

    @Column(name = "ReleaseDate", nullable = false)
    private LocalDateTime releaseDate;

    @Column(name = "RecipientName", length = 255, nullable = false)
    private String recipientName;

    @Column(name = "Purpose", columnDefinition = "NTEXT")
    private String purpose;

    @Lob
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "OperatorID", nullable = false)
    private Personnel operator; // The user/personnel who performed the release

    @Column(name = "CreatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime updatedAt;

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

    public Integer getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(Integer releaseId) {
        this.releaseId = releaseId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Double getQuantityReleased() {
        return quantityReleased;
    }

    public void setQuantityReleased(Double quantityReleased) {
        this.quantityReleased = quantityReleased;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        if (releaseDate == null) {
            releaseDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

