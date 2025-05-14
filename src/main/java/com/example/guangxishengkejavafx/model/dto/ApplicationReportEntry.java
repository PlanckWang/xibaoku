package com.example.guangxishengkejavafx.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReportEntry {
    private String itemName;
    private Long count;
    private String details; // Or any other relevant fields
}

