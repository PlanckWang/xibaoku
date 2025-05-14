package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectType, Integer> {
    Optional<ProjectType> findByProjectTypeName(String projectTypeName);
    List<ProjectType> findByProjectTypeNameContainingIgnoreCase(String projectTypeName);
}

