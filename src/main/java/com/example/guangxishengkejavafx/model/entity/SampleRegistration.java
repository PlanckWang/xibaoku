package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "SampleRegistrations")
public class SampleRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RegistrationID")
    private Integer registrationId;

    @ManyToOne
    @JoinColumn(name = "OrderID") // Nullable based on DB design
    private Order order;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Depositor customer;

    @Column(name = "SampleCode", unique = true, nullable = false, length = 50)
    private String sampleCode;

    @Column(name = "SampleType", nullable = false, length = 50)
    private String sampleType;

    @Column(name = "CollectionDate")
    private LocalDateTime collectionDate;

    @Column(name = "Collector", length = 50)
    private String collector;

    @Column(name = "CollectionSite", length = 100)
    private String collectionSite;

    @Lob
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @Column(name = "Status", length = 20, columnDefinition = "VARCHAR(20) default '待接收'")
    private String status;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime createdAt;

    public Integer getId() { // Added to fulfill plan item 004.i
        return registrationId;
    }

    public String getSampleId() {
        return sampleCode; // Assuming sampleCode is the intended Sample ID for display/logic
    }
    
    // 手动添加getter和setter方法
    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

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

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "待接收";
        }
    }
}

