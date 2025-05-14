package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "FrozenSamples")
public class FrozenSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FrozenSampleID")
    private Integer frozenSampleId;

    // Link to the sample preparation record this frozen sample is derived from
    @ManyToOne
    @JoinColumn(name = "PreparationID", nullable = false)
    private SamplePreparation samplePreparation;

    @Column(name = "FrozenDate", nullable = false)
    private LocalDateTime frozenDate;

    @Column(name = "StorageLocation", length = 255, nullable = false)
    private String storageLocation; // e.g., Tank A, Rack 5, Box 3, Position 12

    @Column(name = "Cryoprotectant", length = 255)
    private String cryoprotectant; // 冻存保护剂

    @Column(name = "Volume")
    private Double volume;

    @Column(name = "Unit", length = 50)
    private String unit; // e.g., mL, vial

    @Column(name = "CellCountPerUnit")
    private Long cellCountPerUnit;

    @Column(name = "PassageNumber")
    private Integer passageNumber;

    @Column(name = "Status", length = 50, nullable = false, columnDefinition = "VARCHAR(50) default '已冻存'")
    private String status; // e.g., 已冻存, 已复苏, 已消耗, 已废弃

    @ManyToOne
    @JoinColumn(name = "OperatorID", nullable = false)
    private Personnel operator;

    @Lob
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime updatedAt;

    public SamplePreparation getSamplePreparation() {
        return samplePreparation;
    }

    public void setSamplePreparation(SamplePreparation samplePreparation) {
        this.samplePreparation = samplePreparation;
    }

    public Integer getFrozenSampleId() {
        return frozenSampleId;
    }

    public void setFrozenSampleId(Integer frozenSampleId) {
        this.frozenSampleId = frozenSampleId;
    }

    public LocalDateTime getFrozenDate() {
        return frozenDate;
    }

    public void setFrozenDate(LocalDateTime frozenDate) {
        this.frozenDate = frozenDate;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getCryoprotectant() {
        return cryoprotectant;
    }

    public void setCryoprotectant(String cryoprotectant) {
        this.cryoprotectant = cryoprotectant;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getCellCountPerUnit() {
        return cellCountPerUnit;
    }

    public void setCellCountPerUnit(Long cellCountPerUnit) {
        this.cellCountPerUnit = cellCountPerUnit;
    }

    public Integer getPassageNumber() {
        return passageNumber;
    }

    public void setPassageNumber(Integer passageNumber) {
        this.passageNumber = passageNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Personnel getOperator() {
        return operator;
    }

    public void setOperator(Personnel operator) {
        this.operator = operator;
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
        if (frozenDate == null) {
            frozenDate = LocalDateTime.now();
        }
        if (status == null) {
            status = "已冻存";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

