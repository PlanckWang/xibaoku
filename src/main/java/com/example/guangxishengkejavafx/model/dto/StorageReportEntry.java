package com.example.guangxishengkejavafx.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageReportEntry {
    private String category; // 例如：产品名称、储户名称、存储位置
    private String subCategory; // 例如：具体产品、具体储户
    private Long count; // 例如：样本数量
    private Double totalVolume; // 例如：样本总体积
    private String details; // 其他相关详细信息
    // 根据具体的报告需求可以添加更多字段
}

