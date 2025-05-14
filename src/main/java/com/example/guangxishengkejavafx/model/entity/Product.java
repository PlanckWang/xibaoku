package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer productId;

    @Column(name = "ProductName", length = 100, nullable = false)
    private String productName;

    @Column(name = "ProductCode", length = 50, unique = true)
    private String productCode;

    @Column(name = "Category", length = 100) // Added Category
    private String category;

    @Column(name = "Specifications", length = 255) // Changed from Specification to Specifications
    private String specifications;

    @Column(name = "Unit", length = 20)
    private String unit;

    @Column(name = "UnitPrice", precision = 18, scale = 2) // Changed from Price to UnitPrice
    private BigDecimal unitPrice;

    @Lob
    @Column(name = "Description", columnDefinition = "NTEXT")
    private String description;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime updatedAt;

    // Constructors
    public Product() {
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductType() { // Added based on compilation error
        return category; // Assuming category is the product type
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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

