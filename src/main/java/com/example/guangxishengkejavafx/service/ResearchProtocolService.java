package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.ResearchProtocol;
import com.example.guangxishengkejavafx.model.repository.ResearchProtocolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResearchProtocolService {

    private final ResearchProtocolRepository researchProtocolRepository;

    @Autowired
    public ResearchProtocolService(ResearchProtocolRepository researchProtocolRepository) {
        this.researchProtocolRepository = researchProtocolRepository;
    }

    public List<ResearchProtocol> findAll() {
        return researchProtocolRepository.findAll();
    }

    public Optional<ResearchProtocol> findById(Integer id) {
        return researchProtocolRepository.findById(id);
    }

    public Optional<ResearchProtocol> findByProtocolCode(String protocolCode) {
        return researchProtocolRepository.findByProtocolCode(protocolCode);
    }

    public List<ResearchProtocol> findByProtocolTitleContaining(String protocolTitle) {
        return researchProtocolRepository.findByProtocolTitleContainingIgnoreCase(protocolTitle);
    }

    public List<ResearchProtocol> findByProjectId(Integer projectId) {
        return researchProtocolRepository.findByProjectId(projectId);
    }

    public List<ResearchProtocol> findByStatus(String status) {
        return researchProtocolRepository.findByStatus(status);
    }

    public List<ResearchProtocol> findByPrincipalInvestigatorId(Integer principalInvestigatorId) {
        return researchProtocolRepository.findByPrincipalInvestigatorId(principalInvestigatorId);
    }

    @Transactional
    public ResearchProtocol save(ResearchProtocol researchProtocol) throws Exception {
        // Handle Protocol Code Uniqueness
        Optional<ResearchProtocol> existingProtocolByCode = researchProtocolRepository.findByProtocolCode(researchProtocol.getProtocolCode());
        if (existingProtocolByCode.isPresent() && (researchProtocol.getProtocolId() == null || !existingProtocolByCode.get().getProtocolId().equals(researchProtocol.getProtocolId()))) {
            throw new Exception("研究方案编号 '" + researchProtocol.getProtocolCode() + "' 已存在。");
        }

        // Set timestamps
        if (researchProtocol.getProtocolId() == null) { // New protocol
            researchProtocol.setCreatedAt(LocalDateTime.now());
            // researchProtocol.setCreatedBy(currentUser.getId()); // Assuming currentUser is available
        } else { // Existing protocol
            researchProtocolRepository.findById(researchProtocol.getProtocolId()).ifPresent(p -> {
                researchProtocol.setCreatedAt(p.getCreatedAt());
                researchProtocol.setCreatedBy(p.getCreatedBy());
            });
        }
        researchProtocol.setUpdatedAt(LocalDateTime.now());
        // researchProtocol.setUpdatedBy(currentUser.getId()); // Assuming currentUser is available

        return researchProtocolRepository.save(researchProtocol);
    }

    @Transactional
    public void deleteById(Integer id) throws Exception {
        if (!researchProtocolRepository.existsById(id)) {
            throw new Exception("无法找到ID为 " + id + " 的研究方案进行删除。");
        }
        // Add any other pre-deletion checks if necessary
        researchProtocolRepository.deleteById(id);
    }

    public List<ResearchProtocol> findAll(Specification<ResearchProtocol> spec) {
        return researchProtocolRepository.findAll(spec);
    }
}

