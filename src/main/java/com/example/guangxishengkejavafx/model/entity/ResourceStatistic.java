package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ResourceStatistics")
public class ResourceStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatisticID")
    private Long id;

    @Column(name = "StatisticName", nullable = false, length = 255)
    private String name;

    @Column(name = "StatisticValue", length = 255)
    private String value;

    @Column(name = "Category", length = 100)
    private String category; // e.g., "物料", "细胞库", "样本", "设备"

    @Column(name = "Description", columnDefinition = "NTEXT")
    private String description;

    @Column(name = "LastCalculatedAt")
    private LocalDateTime lastCalculatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastCalculatedAt() {
        return lastCalculatedAt;
    }

    public void setLastCalculatedAt(LocalDateTime lastCalculatedAt) {
        this.lastCalculatedAt = lastCalculatedAt;
    }
}

