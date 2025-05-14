package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_inbound_records")
@Data
public class MaterialInboundRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "material_id", nullable = false)
    private Integer materialId; // Foreign key to Material table

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "material_id", insertable = false, updatable = false)
    private Material material; // For joining and displaying material name/code

    @Column(name = "inbound_quantity", nullable = false)
    private Integer inboundQuantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice; // Price at the time of inbound

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "inbound_date", nullable = false)
    private LocalDateTime inboundDate;

    @Column(name = "operator_user_id") // User who performed the inbound operation
    private Integer operatorUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User operatorUser; // For joining and displaying operator name

    @Column(name = "supplier_name", length = 255)
    private String supplierName;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalAmount();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        if (this.inboundQuantity != null && this.unitPrice != null) {
            this.totalAmount = this.unitPrice.multiply(new BigDecimal(this.inboundQuantity));
        } else {
            this.totalAmount = null;
        }
    }
    
    // 手动添加getter和setter方法
    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public Integer getInboundQuantity() {
        return inboundQuantity;
    }

    public void setInboundQuantity(Integer inboundQuantity) {
        this.inboundQuantity = inboundQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getInboundDate() {
        return inboundDate;
    }

    public void setInboundDate(LocalDateTime inboundDate) {
        this.inboundDate = inboundDate;
    }

    public Integer getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(Integer operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
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
}

