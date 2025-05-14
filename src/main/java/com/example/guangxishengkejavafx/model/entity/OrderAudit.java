package com.example.guangxishengkejavafx.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "OrderAudits")
public class OrderAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuditID")
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY) // Added Auditor relationship
    @JoinColumn(name = "AuditorUserID")
    private User auditor; // Changed from auditedByUserId (Long) to User entity

    @Column(name = "AuditAction", length = 100)
    private String auditAction; // e.g., "CREATED", "APPROVED", "REJECTED"

    @Column(name = "AuditDate") // Added AuditDate
    private LocalDateTime auditDate;

    @Column(name = "AuditStatus", length = 50) // Added AuditStatus
    private String auditStatus;

    @Lob
    @Column(name = "AuditComments", columnDefinition = "NTEXT") // Added AuditComments
    private String auditComments;

    @Column(name = "AuditTimestamp", nullable = false, updatable = false)
    private LocalDateTime auditTimestamp;

    @Lob
    @Column(name = "Remarks")
    private String remarks;

    @PrePersist
    protected void onCreate() {
        auditTimestamp = LocalDateTime.now();
        if (auditDate == null) { // Default auditDate to now if not set
            auditDate = LocalDateTime.now();
        }
    }

    // Lombok @Data will generate getters and setters for all fields including:
    // getAuditor(), setAuditor(), getAuditDate(), setAuditDate(),
    // getAuditStatus(), setAuditStatus(), getAuditComments(), setAuditComments()
}

