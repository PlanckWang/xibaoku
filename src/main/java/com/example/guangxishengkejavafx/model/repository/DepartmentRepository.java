package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findByDepartmentNameAndInstitutionId(String departmentName, Integer institutionId);

    List<Department> findByInstitutionId(Integer institutionId);

    // Add other custom query methods if needed
}

