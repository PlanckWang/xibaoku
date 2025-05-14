package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SampleCultures")
public class SampleCulture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CultureID")
    private Long cultureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SamplePreparationID")
    private SamplePreparation samplePreparation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OperatorID")
    private Personnel operator;

    @Column(name = "CultureStartDate")
    private LocalDate cultureStartDate;

    @Column(name = "CultureEndDate")
    private LocalDate cultureEndDate;

    @Column(name = "PassageNumber")
    private Integer passageNumber;

    @Column(name = "CultureMedium", length = 255)
    private String cultureMedium;

    @Column(name = "CultureConditions", length = 255)
    private String cultureConditions;

    @Column(name = "CellCount")
    private Long cellCount;

    @Column(name = "Viability")
    private Double viability;

    @Lob
    @Column(name = "Observations", columnDefinition = "NTEXT") // Added Observations field
    private String observations;

    @Lob
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (cultureStartDate == null) {
            cultureStartDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 手动添加getter和setter方法
    public Long getCultureId() {
        return cultureId;
    }

    public void setCultureId(Long cultureId) {
        this.cultureId = cultureId;
    }

    public SamplePreparation getSamplePreparation() {
        return samplePreparation;
    }

    public void setSamplePreparation(SamplePreparation samplePreparation) {
        this.samplePreparation = samplePreparation;
    }

    public Personnel getOperator() {
        return operator;
    }

    public void setOperator(Personnel operator) {
        this.operator = operator;
    }

    public LocalDate getCultureStartDate() {
        return cultureStartDate;
    }

    public void setCultureStartDate(LocalDate cultureStartDate) {
        this.cultureStartDate = cultureStartDate;
    }

    public LocalDate getCultureEndDate() {
        return cultureEndDate;
    }

    public void setCultureEndDate(LocalDate cultureEndDate) {
        this.cultureEndDate = cultureEndDate;
    }

    public Integer getPassageNumber() {
        return passageNumber;
    }

    public void setPassageNumber(Integer passageNumber) {
        this.passageNumber = passageNumber;
    }

    public String getCultureMedium() {
        return cultureMedium;
    }

    public void setCultureMedium(String cultureMedium) {
        this.cultureMedium = cultureMedium;
    }

    public String getCultureConditions() {
        return cultureConditions;
    }

    public void setCultureConditions(String cultureConditions) {
        this.cultureConditions = cultureConditions;
    }

    public Long getCellCount() {
        return cellCount;
    }

    public void setCellCount(Long cellCount) {
        this.cellCount = cellCount;
    }

    public Double getViability() {
        return viability;
    }

    public void setViability(Double viability) {
        this.viability = viability;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

