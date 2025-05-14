package com.example.guangxishengkejavafx.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DerivativeProductReportEntry {
    private String reportName; // e.g., "衍生品类型统计", "特定衍生品产量统计"
    private String derivativeProductName; // 衍生品名称
    private String derivativeProductType; // 衍生品类型 (e.g., DNA, RNA, Protein, Cell Line)
    private String sourceSampleId; // 来源样本编号
    private String sourceSampleType; // 来源样本类型
    private Long quantityProduced; // 生产数量/批次
    private Double totalAmount; // 总量
    private String amountUnit; // 总量单位 (e.g., ug, ml, vials)
    private LocalDate creationDateStart; // 统计周期开始（衍生品创建日期）
    private LocalDate creationDateEnd; // 统计周期结束（衍生品创建日期）
    private String createdBy; // 创建人/部门
    private String details; // 其他相关详细信息
    // 根据具体的报告需求可以添加更多字段
}

