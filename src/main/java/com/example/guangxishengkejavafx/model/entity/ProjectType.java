package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ProjectTypes")
public class ProjectType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectTypeID")
    private Integer projectTypeId;

    @Column(name = "ProjectTypeName", length = 100, nullable = false, unique = true)
    private String projectTypeName;

    @Column(name = "Description", columnDefinition = "NTEXT")
    private String description;

    // Getters and Setters
    public Integer getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(Integer projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public String getProjectTypeName() {
        return projectTypeName;
    }

    public void setProjectTypeName(String projectTypeName) {
        this.projectTypeName = projectTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

