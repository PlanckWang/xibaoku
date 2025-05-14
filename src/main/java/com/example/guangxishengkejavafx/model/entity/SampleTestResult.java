package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "SampleTestResults")
public class SampleTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResultID")
    private Integer resultId;

    @OneToOne // Assuming one request leads to one set of results, adjust if one-to-many
    @JoinColumn(name = "RequestID", nullable = false, unique = true)
    private SampleInspectionRequest sampleInspectionRequest;

    @Column(name = "TestDate")
    private LocalDateTime testDate;

    @ManyToOne
    @JoinColumn(name = "TesterID") // Assuming Tester is a Personnel
    private Personnel tester;

    @Lob
    @Column(name = "TestResults", columnDefinition = "NTEXT") // Storing as text, could be JSON or structured data
    private String testResults;

    @Column(name = "Conclusion", length = 255)
    private String conclusion;

    @Column(name = "Status", length = 20, nullable = false, columnDefinition = "VARCHAR(20) default '待审核'")
    private String status; // e.g., 待审核, 合格, 不合格, 需复检

    @Lob
    @Column(name = "Notes", columnDefinition = "NTEXT")
    private String notes;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME default GETDATE()")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (testDate == null) {
            testDate = LocalDateTime.now(); // Default to now if not specified
        }
        if (status == null) {
            status = "待审核";
        }
    }
}

