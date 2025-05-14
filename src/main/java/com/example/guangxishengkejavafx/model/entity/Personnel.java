package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Personnel") // Ensure this matches your SQLServer table name
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PersonnelID")
    private Integer personnelId;

    @Column(name = "Name", length = 100) // Added Name for display purposes
    private String name;

    @Column(name = "UserID", unique = true, nullable = false)    private Integer userId; // Foreign key to Users table
    // In a more complete setup, this would be:
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "UserID", nullable = false, unique = true)
    // private User user;

    @Column(name = "EmployeeID", length = 50, unique = true)
    private String employeeId;

    @Column(name = "Position", length = 50)
    private String position;

    @Column(name = "HireDate")
    private LocalDate hireDate;

    @Lob // For NTEXT or similar large text types
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    // Assuming CreatedAt and UpdatedAt are managed by the User entity or not directly needed here
    // If they are needed specifically for Personnel changes, they can be added.

    // Constructors
    public Personnel() {
    }

    // Getters and Setters
    public Integer getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(Integer personnelId) {
        this.personnelId = personnelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

