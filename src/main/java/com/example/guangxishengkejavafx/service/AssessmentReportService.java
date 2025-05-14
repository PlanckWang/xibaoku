package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.AssessmentReport;
import com.example.guangxishengkejavafx.model.repository.AssessmentReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssessmentReportService {

    private final AssessmentReportRepository assessmentReportRepository;

    @Autowired
    public AssessmentReportService(AssessmentReportRepository assessmentReportRepository) {
        this.assessmentReportRepository = assessmentReportRepository;
    }

    public List<AssessmentReport> findAll() {
        return assessmentReportRepository.findAll();
    }

    public Optional<AssessmentReport> findById(Integer id) {
        return assessmentReportRepository.findById(id);
    }

    public List<AssessmentReport> findByReportTitleContaining(String reportTitle) {
        return assessmentReportRepository.findByReportTitleContainingIgnoreCase(reportTitle);
    }

    public List<AssessmentReport> findByProtocolId(Integer protocolId) {
        return assessmentReportRepository.findByProtocolId(protocolId);
    }

    public List<AssessmentReport> findByPatientId(Integer patientId) {
        return assessmentReportRepository.findByPatientId(patientId);
    }

    public List<AssessmentReport> findByAssessorId(Integer assessorId) {
        return assessmentReportRepository.findByAssessorId(assessorId);
    }

    public List<AssessmentReport> findByStatus(String status) {
        return assessmentReportRepository.findByStatus(status);
    }

    public List<AssessmentReport> findByReportTypeContaining(String reportType) {
        return assessmentReportRepository.findByReportTypeContainingIgnoreCase(reportType);
    }

    @Transactional
    public AssessmentReport save(AssessmentReport assessmentReport) throws Exception {
        // Basic validation (can be expanded)
        if (assessmentReport.getReportTitle() == null || assessmentReport.getReportTitle().trim().isEmpty()) {
            throw new Exception("报告标题不能为空。");
        }
        if (assessmentReport.getProtocolId() == null) {
            throw new Exception("关联研究方案ID不能为空。");
        }
        if (assessmentReport.getPatientId() == null) {
            throw new Exception("关联患者ID不能为空。");
        }
        if (assessmentReport.getAssessmentDate() == null) {
            throw new Exception("评估日期不能为空。");
        }

        // Set timestamps
        if (assessmentReport.getReportId() == null) { // New report
            assessmentReport.setCreatedAt(LocalDateTime.now());
            // assessmentReport.setCreatedBy(currentUser.getId()); // Assuming currentUser is available
        } else { // Existing report
            assessmentReportRepository.findById(assessmentReport.getReportId()).ifPresent(ar -> {
                assessmentReport.setCreatedAt(ar.getCreatedAt());
                assessmentReport.setCreatedBy(ar.getCreatedBy());
            });
        }
        assessmentReport.setUpdatedAt(LocalDateTime.now());
        // assessmentReport.setUpdatedBy(currentUser.getId()); // Assuming currentUser is available

        return assessmentReportRepository.save(assessmentReport);
    }

    @Transactional
    public void deleteById(Integer id) throws Exception {
        if (!assessmentReportRepository.existsById(id)) {
            throw new Exception("无法找到ID为 " + id + " 的评估报告进行删除。");
        }
        // Add any other pre-deletion checks if necessary
        assessmentReportRepository.deleteById(id);
    }

    public List<AssessmentReport> findAll(Specification<AssessmentReport> spec) {
        return assessmentReportRepository.findAll(spec);
    }
}

