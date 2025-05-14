package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.ProjectType;
import com.example.guangxishengkejavafx.model.repository.ProjectTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectTypeService {

    private final ProjectTypeRepository projectTypeRepository;

    @Autowired
    public ProjectTypeService(ProjectTypeRepository projectTypeRepository) {
        this.projectTypeRepository = projectTypeRepository;
    }

    public List<ProjectType> findAll() {
        return projectTypeRepository.findAll();
    }

    public Optional<ProjectType> findById(Integer id) {
        return projectTypeRepository.findById(id);
    }

    public Optional<ProjectType> findByProjectTypeName(String projectTypeName) {
        return projectTypeRepository.findByProjectTypeName(projectTypeName);
    }

    public List<ProjectType> findByProjectTypeNameContaining(String projectTypeName) {
        return projectTypeRepository.findByProjectTypeNameContainingIgnoreCase(projectTypeName);
    }

    public ProjectType save(ProjectType projectType) throws Exception {
        // Check for uniqueness of projectTypeName before saving
        Optional<ProjectType> existingType = projectTypeRepository.findByProjectTypeName(projectType.getProjectTypeName());
        if (existingType.isPresent() && (projectType.getProjectTypeId() == null || !existingType.get().getProjectTypeId().equals(projectType.getProjectTypeId()))) {
            throw new Exception("项目类型名称 '" + projectType.getProjectTypeName() + "' 已存在。");
        }
        return projectTypeRepository.save(projectType);
    }

    public void deleteById(Integer id) throws Exception {
        // Add any business logic before deletion, e.g., check if this type is used by any projects
        // For now, directly delete.
        if (!projectTypeRepository.existsById(id)) {
            throw new Exception("无法找到ID为 " + id + " 的项目类型进行删除。");
        }
        projectTypeRepository.deleteById(id);
    }
}

