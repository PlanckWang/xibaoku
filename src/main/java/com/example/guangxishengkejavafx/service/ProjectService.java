package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Project;
import com.example.guangxishengkejavafx.model.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Optional<Project> findById(Integer id) {
        return projectRepository.findById(id);
    }

    public Optional<Project> findByProjectCode(String projectCode) {
        return projectRepository.findByProjectCode(projectCode);
    }

    public List<Project> findByProjectNameContaining(String projectName) {
        return projectRepository.findByProjectNameContainingIgnoreCase(projectName);
    }

    public List<Project> findByProjectTypeId(Integer projectTypeId) {
        return projectRepository.findByProjectTypeId(projectTypeId);
    }

    public List<Project> findByStatus(String status) {
        return projectRepository.findByStatus(status);
    }

    public List<Project> findByPrincipalInvestigatorId(Integer principalInvestigatorId) {
        return projectRepository.findByPrincipalInvestigatorId(principalInvestigatorId);
    }

    @Transactional
    public Project save(Project project) throws Exception {
        // Handle Project Code Uniqueness
        Optional<Project> existingProjectByCode = projectRepository.findByProjectCode(project.getProjectCode());
        if (existingProjectByCode.isPresent() && (project.getProjectId() == null || !existingProjectByCode.get().getProjectId().equals(project.getProjectId()))) {
            throw new Exception("项目编号 '" + project.getProjectCode() + "' 已存在。");
        }

        // Set timestamps
        if (project.getProjectId() == null) { // New project
            project.setCreatedAt(LocalDateTime.now());
            // project.setCreatedBy(currentUser.getId()); // Assuming currentUser is available
        } else { // Existing project
            // Keep original createdAt and createdBy
            projectRepository.findById(project.getProjectId()).ifPresent(p -> {
                project.setCreatedAt(p.getCreatedAt());
                project.setCreatedBy(p.getCreatedBy());
            });
        }
        project.setUpdatedAt(LocalDateTime.now());
        // project.setUpdatedBy(currentUser.getId()); // Assuming currentUser is available

        return projectRepository.save(project);
    }

    @Transactional
    public void deleteById(Integer id) throws Exception {
        if (!projectRepository.existsById(id)) {
            throw new Exception("无法找到ID为 " + id + " 的项目进行删除。");
        }
        // Add any other pre-deletion checks if necessary (e.g., related tasks, etc.)
        projectRepository.deleteById(id);
    }

    // Method to use JpaSpecificationExecutor if needed for more complex searches
    public List<Project> findAll(Specification<Project> spec) {
        return projectRepository.findAll(spec);
    }
}

