package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "Materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaterialID")
    private Integer materialId;

    @Column(name = "MaterialName", length = 100, nullable = false)
    private String materialName;

    @Column(name = "MaterialCode", length = 50, unique = true)
    private String materialCode;

    @Column(name = "Category", length = 100) // Added Category
    private String category;

    @Column(name = "Specifications", length = 255) // Renamed from Specification
    private String specifications;

    @Column(name = "Unit", length = 20)
    private String unit;

    @Column(name = "Supplier", length = 100)
    private String supplier;

    @Column(name = "UnitPrice", precision = 10, scale = 2) // Added UnitPrice
    private BigDecimal unitPrice;

    @Column(name = "StockQuantity", columnDefinition = "INT DEFAULT 0") // Renamed from CurrentStock
    private Integer stockQuantity = 0;

    @Column(name = "WarningStockLevel", columnDefinition = "INT DEFAULT 0")
    private Integer warningStockLevel = 0;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime updatedAt;

    @Lob // For NTEXT or similar large text types
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes; // Added Notes field

    // Constructors
    public Material() {
    }

    // Getters and Setters
    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getWarningStockLevel() {
        return warningStockLevel;
    }

    public void setWarningStockLevel(Integer warningStockLevel) {
        this.warningStockLevel = warningStockLevel;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

