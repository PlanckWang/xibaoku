package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "StorageContracts")
public class StorageContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ContractID")
    private Integer contractId;

    @Column(name = "ContractNumber", unique = true, nullable = false, length = 50)
    private String contractNumber;

    @ManyToOne
    @JoinColumn(name = "OrderID") // Nullable based on DB design
    private Order order;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Depositor customer;

    @Column(name = "ContractSignDate")
    private LocalDate contractSignDate;

    @Column(name = "ContractStartDate")
    private LocalDate contractStartDate;

    @Column(name = "ContractEndDate")
    private LocalDate contractEndDate;

    @Column(name = "StorageFee", precision = 18, scale = 2)
    private BigDecimal storageFee;

    @Column(name = "PaymentStatus", length = 20)
    private String paymentStatus;

    @Lob
    @Column(name = "ContractTerms", columnDefinition = "NTEXT")
    private String contractTerms;

    @Column(name = "FilePath", length = 255)
    private String filePath;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime updatedAt;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Depositor getCustomer() {
        return customer;
    }

    public void setCustomer(Depositor customer) {
        this.customer = customer;
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

