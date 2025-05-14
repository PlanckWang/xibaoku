package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "SampleInspectionRequests")
public class SampleInspectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestID")
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "RegistrationID", nullable = false)
    private SampleRegistration sampleRegistration;

    @Column(name = "RequestDate", nullable = false)
    private LocalDateTime requestDate;

    @ManyToOne
    @JoinColumn(name = "RequesterID") // Assuming Requester is a Personnel
    private Personnel requester;

    @Lob
    @Column(name = "InspectionItems", columnDefinition = "NTEXT") // Storing as text, could be JSON or comma-separated
    private String inspectionItems;

    @Column(name = "Status", length = 20, nullable = false, columnDefinition = "VARCHAR(20) default '待审批'")
    private String status;

    @Lob
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime createdAt;

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public SampleRegistration getSampleRegistration() {
        return sampleRegistration;
    }

    public void setSampleRegistration(SampleRegistration sampleRegistration) {
        this.sampleRegistration = sampleRegistration;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public Personnel getRequester() {
        return requester;
    }

    public void setRequester(Personnel requester) {
        this.requester = requester;
    }

    public String getInspectionItems() {
        return inspectionItems;
    }

    public void setInspectionItems(String inspectionItems) {
        this.inspectionItems = inspectionItems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (requestDate == null) {
            requestDate = LocalDateTime.now();
        }
        if (status == null) {
            status = "待审批";
        }
    }
}

