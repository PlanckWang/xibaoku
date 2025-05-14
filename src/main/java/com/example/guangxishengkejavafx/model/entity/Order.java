package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "SampleOrders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;

    @Column(name = "OrderNumber", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Depositor customer;

    @Column(name = "OrderDate", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "ProductID")
    private Product product;

    @Column(name = "SampleType", length = 50)
    private String sampleType;

    @Column(name = "Quantity", columnDefinition = "INT default 1")
    private Integer quantity;

    @Column(name = "TotalAmount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "OrderStatus", nullable = false, length = 20)
    private String orderStatus;

    @ManyToOne
    @JoinColumn(name = "SalespersonID")
    private User salesperson;

    @Lob
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (quantity == null) {
            quantity = 1;
        }
    }
    
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public Depositor getCustomer() {
        return customer;
    }
    
    public void setCustomer(Depositor customer) {
        this.customer = customer;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public User getSalesperson() {
        return salesperson;
    }
    
    public void setSalesperson(User salesperson) {
        this.salesperson = salesperson;
    }
}

