package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.ResearchProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResearchProtocolRepository extends JpaRepository<ResearchProtocol, Integer>, JpaSpecificationExecutor<ResearchProtocol> {
    Optional<ResearchProtocol> findByProtocolCode(String protocolCode);
    List<ResearchProtocol> findByProtocolTitleContainingIgnoreCase(String protocolTitle);
    List<ResearchProtocol> findByProjectId(Integer projectId);
    List<ResearchProtocol> findByStatus(String status);
    List<ResearchProtocol> findByPrincipalInvestigatorId(Integer principalInvestigatorId);
}

