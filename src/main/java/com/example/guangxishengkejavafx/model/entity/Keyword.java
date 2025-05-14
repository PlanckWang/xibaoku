package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Keywords", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"keywordText", "category"}) // Assuming keyword is unique within a category
})
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KeywordID")
    private Long id;

    @Column(name = "KeywordText", nullable = false, length = 255)
    private String keywordText;

    @Column(name = "Category", length = 100)
    private String category; // e.g., "文献", "专利", "临床研究", "通用"

    @Column(name = "Description", columnDefinition = "NTEXT")
    private String description;

    @Column(name = "Frequency")
    private Integer frequency; // Optional: how often it's used or searched

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedBy")
    private Integer updatedBy;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeywordText() {
        return keywordText;
    }

    public void setKeywordText(String keywordText) {
        this.keywordText = keywordText;
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

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

