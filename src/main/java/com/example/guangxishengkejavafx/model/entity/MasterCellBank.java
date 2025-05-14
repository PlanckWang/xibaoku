package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "master_cell_bank")
public class MasterCellBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bankId; // 主细胞库编号，例如 MCB-YYYYMMDD-001

    @ManyToOne
    @JoinColumn(name = "original_cell_bank_id") // Can be linked to an original cell bank
    private OriginalCellBank originalCellBank;

    @ManyToOne
    @JoinColumn(name = "sample_preparation_id") // Or linked to a sample preparation record
    private SamplePreparation samplePreparation; // 关联的样本制备记录 (if applicable)

    @ManyToOne
    @JoinColumn(name = "sample_registration_id", nullable = false) // Still need to know the ultimate sample source
    private SampleRegistration sampleRegistration;

    @Column(nullable = false)
    private String cellLineName; // 细胞系名称

    private String cellType; // 细胞类型

    @Column(nullable = false)
    private Integer passageNumber; // 传代次数 (e.g., P5)

    @Column(nullable = false)
    private LocalDate cryopreservationDate; // 冻存日期

    @Column(nullable = false)
    private Integer numberOfVials; // 冻存管数

    private Long cellsPerVial; // 每管细胞数

    private Double volumePerVial; // 每管体积

    @Column(length = 50)
    private String volumeUnit; // 体积单位

    private String cryopreservationMedium; // 冻存液类型

    @Column(nullable = false)
    private String storageLocation; // 存储位置

    private String operator; // 操作员

    @Column(length = 50)
    private String qualityControlStatus; // 质量控制状态

    @Column(length = 50)
    private String mycoplasmaTestResult; // 支原体检测结果

    @Column(length = 50)
    private String sterilityTestResult; // 无菌检测结果

    private Double viabilityPostThaw; // 复苏后活率 (%)

    private Integer passageLimit; // 传代限次

    @Lob
    @Column(columnDefinition = "TEXT")
    private String remarks; // 备注

    @Column(length = 50, nullable = false)
    private String status; // 状态 (e.g., "可用", "已用尽", "检定中", "已废弃")

    private LocalDate entryDate; // 入库登记日期

    private String createdBy; // 创建人

    @CreationTimestamp
    private LocalDateTime creationTimestamp; // 创建时间

    private String lastModifiedBy; // 最后修改人

    @UpdateTimestamp
    private LocalDateTime lastModifiedTimestamp; // 最后修改时间
}

