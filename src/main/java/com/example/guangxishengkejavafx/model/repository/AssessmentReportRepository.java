package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.AssessmentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentReportRepository extends JpaRepository<AssessmentReport, Integer>, JpaSpecificationExecutor<AssessmentReport> {
    List<AssessmentReport> findByReportTitleContainingIgnoreCase(String reportTitle);
    List<AssessmentReport> findByProtocolId(Integer protocolId);
    List<AssessmentReport> findByPatientId(Integer patientId);
    List<AssessmentReport> findByAssessorId(Integer assessorId);
    List<AssessmentReport> findByStatus(String status);
    List<AssessmentReport> findByReportTypeContainingIgnoreCase(String reportType);
}

