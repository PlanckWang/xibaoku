package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CellBanks") // Assuming WorkingCellBank uses the same table as Original and Master
public class CellBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CellBankID")
    private Integer cellBankId;

    @Column(name = "CryoSampleID")
    private Integer cryoSampleId;

    @Column(name = "BankType", nullable = false)
    private String bankType = "工作细胞库"; // Default value for WorkingCellBank

    @Column(name = "CellLineName")
    private String cellLineName;

    @Column(name = "PassageNumber")
    private Integer passageNumber;

    @Column(name = "CellCount")
    private Long cellCount;

    @Column(name = "Viability")
    private Double viability;

    @Column(name = "StorageDate")
    private LocalDateTime storageDate;

    @Column(name = "StorageLocation")
    private String storageLocation;

    @Column(name = "OperatorID", nullable = false)
    private Integer operatorId;

    @Column(name = "Status")
    private String status;

    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    // Getters and Setters
    public Integer getCellBankId() {
        return cellBankId;
    }

    public void setCellBankId(Integer cellBankId) {
        this.cellBankId = cellBankId;
    }

    public Integer getCryoSampleId() {
        return cryoSampleId;
    }

    public void setCryoSampleId(Integer cryoSampleId) {
        this.cryoSampleId = cryoSampleId;
    }

    public String getBankType() {
        return bankType;
    }

    // No setter for bankType as it's fixed for this entity
    // public void setBankType(String bankType) {
    //     this.bankType = bankType;
    // }

    public String getCellLineName() {
        return cellLineName;
    }

    public void setCellLineName(String cellLineName) {
        this.cellLineName = cellLineName;
    }

    public Integer getPassageNumber() {
        return passageNumber;
    }

    public void setPassageNumber(Integer passageNumber) {
        this.passageNumber = passageNumber;
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

    public LocalDateTime getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(LocalDateTime storageDate) {
        this.storageDate = storageDate;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

