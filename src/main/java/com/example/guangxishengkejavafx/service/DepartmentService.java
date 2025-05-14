package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Department;
import com.example.guangxishengkejavafx.model.repository.DepartmentRepository;
import com.example.guangxishengkejavafx.model.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final InstitutionRepository institutionRepository; // To validate institution existence

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, InstitutionRepository institutionRepository) {
        this.departmentRepository = departmentRepository;
        this.institutionRepository = institutionRepository;
    }

    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Department> getDepartmentById(Integer id) {
        return departmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Department> getDepartmentsByInstitutionId(Integer institutionId) {
        return departmentRepository.findByInstitutionId(institutionId);
    }

    @Transactional
    public Department saveDepartment(Department department) {
        // Validate if the institution exists
        if (!institutionRepository.existsById(department.getInstitutionId())) {
            throw new RuntimeException("Institution not found with id: " + department.getInstitutionId());
        }
        // Check for duplicate department name within the same institution
        Optional<Department> existingDept = departmentRepository.findByDepartmentNameAndInstitutionId(department.getDepartmentName(), department.getInstitutionId());
        if (existingDept.isPresent()) {
            throw new RuntimeException("Department with name '" + department.getDepartmentName() + "' already exists in this institution.");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Integer id, Department departmentDetails) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Validate if the institution exists (if institutionId is being changed, though typically it's fixed)
        if (!department.getInstitutionId().equals(departmentDetails.getInstitutionId())) {
            if (!institutionRepository.existsById(departmentDetails.getInstitutionId())) {
                throw new RuntimeException("New Institution not found with id: " + departmentDetails.getInstitutionId());
            }
            department.setInstitutionId(departmentDetails.getInstitutionId());
        }
        
        // Check for duplicate department name within the same institution, excluding the current department
        Optional<Department> existingDept = departmentRepository.findByDepartmentNameAndInstitutionId(departmentDetails.getDepartmentName(), department.getInstitutionId());
        if (existingDept.isPresent() && !existingDept.get().getDepartmentId().equals(id)) {
            throw new RuntimeException("Another department with name '" + departmentDetails.getDepartmentName() + "' already exists in this institution.");
        }

        department.setDepartmentName(departmentDetails.getDepartmentName());
        department.setDescription(departmentDetails.getDescription());
        // UpdatedAt will be handled by @PreUpdate in the entity
        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Integer id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        // Add checks for dependencies (e.g., if personnel are assigned to this department) before deleting
        // For example: if (personnelRepository.existsByDepartmentId(id)) { throw new RuntimeException("Cannot delete department with assigned personnel."); }
        departmentRepository.deleteById(id);
    }
}

