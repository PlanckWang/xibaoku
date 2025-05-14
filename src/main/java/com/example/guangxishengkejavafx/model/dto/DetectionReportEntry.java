package com.example.guangxishengkejavafx.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectionReportEntry {
    private String reportName; // e.g., "特定检测项目阳性率统计", "样本类型合格率统计"
    private String testItem; // e.g., "HIV抗体检测", "细胞活率检测"
    private String sampleType; // e.g., "血清", "培养细胞"
    private Long totalSamples; // 总样本数
    private Long positiveSamples; // 阳性/合格样本数
    private Double positiveRate; // 阳性率/合格率
    private String resultUnit; // 结果单位 (如果适用)
    private Double averageValue; // 平均值 (如果适用)
    private LocalDate startDate; // 统计周期开始
    private LocalDate endDate; // 统计周期结束
    private String details; // 其他相关详细信息
    // 根据具体的报告需求可以添加更多字段
}

