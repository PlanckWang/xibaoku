package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project> {
    Optional<Project> findByProjectCode(String projectCode);
    List<Project> findByProjectNameContainingIgnoreCase(String projectName);
    List<Project> findByProjectTypeId(Integer projectTypeId);
    List<Project> findByStatus(String status);
    List<Project> findByPrincipalInvestigatorId(Integer principalInvestigatorId);
}

