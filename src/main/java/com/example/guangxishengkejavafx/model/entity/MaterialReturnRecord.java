package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_return_records") // "退库记录"
@Data
public class MaterialReturnRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "material_id", nullable = false)
    private Integer materialId; // Foreign key to Material table

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "material_id", insertable = false, updatable = false)
    private Material material; // For joining and displaying material name/code

    @Column(name = "return_quantity", nullable = false)
    private Integer returnQuantity;

    @Column(name = "unit_price", precision = 10, scale = 2) // Price at the time of return, could be original inbound price or current
    private BigDecimal unitPrice;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason; // Reason for return

    @Column(name = "operator_user_id") // User who performed the return operation
    private Integer operatorUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User operatorUser; // For joining and displaying operator name
    
    @Column(name = "related_inbound_record_id") // Optional: link to original inbound record if applicable
    private Integer relatedInboundRecordId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
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

    public Integer getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
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

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(Integer operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public Integer getRelatedInboundRecordId() {
        return relatedInboundRecordId;
    }

    public void setRelatedInboundRecordId(Integer relatedInboundRecordId) {
        this.relatedInboundRecordId = relatedInboundRecordId;
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
        if (this.returnQuantity != null && this.unitPrice != null) {
            this.totalAmount = this.unitPrice.multiply(new BigDecimal(this.returnQuantity));
        } else {
            this.totalAmount = null;
        }
    }
}

