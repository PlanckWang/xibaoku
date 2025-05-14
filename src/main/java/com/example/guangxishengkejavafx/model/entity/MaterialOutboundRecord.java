package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_outbound_records") // "出库记录"
@Data
public class MaterialOutboundRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "material_id", nullable = false)
    private Integer materialId; // Foreign key to Material table

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "material_id", insertable = false, updatable = false)
    private Material material; // For joining and displaying material name/code

    @Column(name = "outbound_quantity", nullable = false)
    private Integer outboundQuantity;

    @Column(name = "unit_price", precision = 10, scale = 2) // Price at the time of outbound, could be current material price or a specific project price
    private BigDecimal unitPrice;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "outbound_date", nullable = false)
    private LocalDateTime outboundDate;

    @Column(name = "recipient_department_id") // e.g., Department that receives the material
    private Integer recipientDepartmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_department_id", referencedColumnName = "department_id", insertable = false, updatable = false)
    private Department recipientDepartment;

    @Column(name = "recipient_personnel_id") // e.g., Personnel who receives the material
    private Integer recipientPersonnelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_personnel_id", referencedColumnName = "personnel_id", insertable = false, updatable = false)
    private Personnel recipientPersonnel;

    @Column(name = "purpose", columnDefinition = "TEXT") // Purpose of outbound (e.g., project use, research)
    private String purpose;

    @Column(name = "operator_user_id") // User who performed the outbound operation
    private Integer operatorUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User operatorUser; // For joining and displaying operator name

    @Column(name = "related_project_id") // Optional: link to a project if applicable
    private Integer relatedProjectId;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "related_project_id", referencedColumnName = "project_id", insertable = false, updatable = false)
    // private Project project; // Assuming a Project entity exists

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
        if (this.outboundQuantity != null && this.unitPrice != null) {
            this.totalAmount = this.unitPrice.multiply(new BigDecimal(this.outboundQuantity));
        } else {
            this.totalAmount = null;
        }
    }
}

