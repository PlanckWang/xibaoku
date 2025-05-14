package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CellProductionPlans")
public class CellProductionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlanID")
    private Integer planId;

    @Column(name = "PlanName", length = 100)
    private String planName;

    @Column(name = "TargetProduct", length = 100)
    private String targetProduct;

    @Column(name = "RequiredCellBankID")
    private Integer requiredCellBankId;

    @Column(name = "PlannedQuantity", length = 50)
    private String plannedQuantity;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "CreatorID", nullable = false)
    private Integer creatorId;

    // Getters and Setters
    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getTargetProduct() {
        return targetProduct;
    }

    public void setTargetProduct(String targetProduct) {
        this.targetProduct = targetProduct;
    }

    public Integer getRequiredCellBankId() {
        return requiredCellBankId;
    }

    public void setRequiredCellBankId(Integer requiredCellBankId) {
        this.requiredCellBankId = requiredCellBankId;
    }

    public String getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(String plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }
}

