package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "original_cell_bank")
public class OriginalCellBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bankId; // 细胞库编号，例如 OCB-YYYYMMDD-001

    @ManyToOne
    @JoinColumn(name = "sample_registration_id", nullable = false)
    private SampleRegistration sampleRegistration; // 关联的样本登记信息

    @Column(nullable = false)
    private String cellLineName; // 细胞系名称

    private String cellType; // 细胞类型 (e.g., MSC, T-Cell, Fibroblast)

    private Integer passageNumber; // 传代次数 (e.g., P0, P1)

    @Column(nullable = false)
    private LocalDate cryopreservationDate; // 冻存日期

    @Column(nullable = false)
    private Integer numberOfVials; // 冻存管数

    private Long cellsPerVial; // 每管细胞数 (e.g., 1000000 for 1x10^6)

    private Double volumePerVial; // 每管体积

    @Column(length = 50)
    private String volumeUnit; // 体积单位 (e.g., "mL")

    private String cryopreservationMedium; // 冻存液类型

    @Column(nullable = false)
    private String storageLocation; // 存储位置 (e.g., "LN2 Tank A, Rack 1, Box 5, Pos 3")

    private String operator; // 操作员

    @Column(length = 50)
    private String qualityControlStatus; // 质量控制状态 (e.g., "合格", "待检", "不合格")

    @Column(length = 50)
    private String mycoplasmaTestResult; // 支原体检测结果

    @Column(length = 50)
    private String sterilityTestResult; // 无菌检测结果

    private Double viabilityPostThaw; // 复苏后活率 (%)

    @Lob
    @Column(columnDefinition = "TEXT")
    private String remarks; // 备注

    @Column(length = 50, nullable = false)
    private String status; // 状态 (e.g., "可用", "已用尽", "隔离中", "已废弃")

    private LocalDate entryDate; // 入库登记日期

    private String createdBy; // 创建人

    @CreationTimestamp
    private LocalDateTime creationTimestamp; // 创建时间

    private String lastModifiedBy; // 最后修改人

    @UpdateTimestamp
    private LocalDateTime lastModifiedTimestamp; // 最后修改时间
    
    // 手动添加getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public SampleRegistration getSampleRegistration() {
        return sampleRegistration;
    }

    public void setSampleRegistration(SampleRegistration sampleRegistration) {
        this.sampleRegistration = sampleRegistration;
    }

    public String getCellLineName() {
        return cellLineName;
    }

    public void setCellLineName(String cellLineName) {
        this.cellLineName = cellLineName;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public Integer getPassageNumber() {
        return passageNumber;
    }

    public void setPassageNumber(Integer passageNumber) {
        this.passageNumber = passageNumber;
    }

    public LocalDate getCryopreservationDate() {
        return cryopreservationDate;
    }

    public void setCryopreservationDate(LocalDate cryopreservationDate) {
        this.cryopreservationDate = cryopreservationDate;
    }

    public Integer getNumberOfVials() {
        return numberOfVials;
    }

    public void setNumberOfVials(Integer numberOfVials) {
        this.numberOfVials = numberOfVials;
    }

    public Long getCellsPerVial() {
        return cellsPerVial;
    }

    public void setCellsPerVial(Long cellsPerVial) {
        this.cellsPerVial = cellsPerVial;
    }

    public Double getVolumePerVial() {
        return volumePerVial;
    }

    public void setVolumePerVial(Double volumePerVial) {
        this.volumePerVial = volumePerVial;
    }

    public String getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(String volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    public String getCryopreservationMedium() {
        return cryopreservationMedium;
    }

    public void setCryopreservationMedium(String cryopreservationMedium) {
        this.cryopreservationMedium = cryopreservationMedium;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getQualityControlStatus() {
        return qualityControlStatus;
    }

    public void setQualityControlStatus(String qualityControlStatus) {
        this.qualityControlStatus = qualityControlStatus;
    }

    public String getMycoplasmaTestResult() {
        return mycoplasmaTestResult;
    }

    public void setMycoplasmaTestResult(String mycoplasmaTestResult) {
        this.mycoplasmaTestResult = mycoplasmaTestResult;
    }

    public String getSterilityTestResult() {
        return sterilityTestResult;
    }

    public void setSterilityTestResult(String sterilityTestResult) {
        this.sterilityTestResult = sterilityTestResult;
    }

    public Double getViabilityPostThaw() {
        return viabilityPostThaw;
    }

    public void setViabilityPostThaw(Double viabilityPostThaw) {
        this.viabilityPostThaw = viabilityPostThaw;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}

