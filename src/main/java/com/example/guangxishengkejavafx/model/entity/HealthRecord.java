package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "HealthRecords")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecordID")
    private Integer recordId;

    @Column(name = "PatientID", nullable = false) // Assuming this links to a Depositor or Personnel ID
    private Integer patientId;

    @Column(name = "RecordDate", nullable = false)
    private LocalDate recordDate;

    @Column(name = "RecordType", length = 100) // e.g., "体检报告", "随访记录", "用药记录"
    private String recordType;

    @Column(name = "Description", columnDefinition = "NTEXT")
    private String description;

    @Column(name = "Height", precision = 5, scale = 2) // cm
    private Double height;

    @Column(name = "Weight", precision = 5, scale = 2) // kg
    private Double weight;

    @Column(name = "BloodPressure", length = 20) // e.g., "120/80"
    private String bloodPressure;

    @Column(name = "HeartRate") // bpm
    private Integer heartRate;

    @Column(name = "BloodSugar", precision = 5, scale = 2) // mmol/L
    private Double bloodSugar;

    @Column(name = "Symptoms", columnDefinition = "NTEXT")
    private String symptoms;

    @Column(name = "Diagnosis", columnDefinition = "NTEXT")
    private String diagnosis;

    @Column(name = "TreatmentPlan", columnDefinition = "NTEXT")
    private String treatmentPlan;

    @Column(name = "Medication", columnDefinition = "NTEXT")
    private String medication;

    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @Column(name = "RecordedBy") // User ID of the recorder
    private Integer recordedBy;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Double getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(Double bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Integer recordedBy) {
        this.recordedBy = recordedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

