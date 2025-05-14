package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Institution;
import com.example.guangxishengkejavafx.model.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    @Autowired
    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @Transactional(readOnly = true)
    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Institution> getInstitutionById(Integer id) {
        return institutionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Institution> getInstitutionByName(String name) {
        return institutionRepository.findByInstitutionName(name);
    }

    @Transactional
    public Institution saveInstitution(Institution institution) {
        // Add any business logic before saving, e.g., validation
        return institutionRepository.save(institution);
    }

    @Transactional
    public Institution updateInstitution(Integer id, Institution institutionDetails) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id)); // Consider a custom exception

        institution.setInstitutionName(institutionDetails.getInstitutionName());
        institution.setAddress(institutionDetails.getAddress());
        institution.setContactPerson(institutionDetails.getContactPerson());
        institution.setContactPhone(institutionDetails.getContactPhone());
        // UpdatedAt will be handled by @PreUpdate
        return institutionRepository.save(institution);
    }

    @Transactional
    public void deleteInstitution(Integer id) {
        if (!institutionRepository.existsById(id)) {
            throw new RuntimeException("Institution not found with id: " + id); // Consider a custom exception
        }
        institutionRepository.deleteById(id);
    }
}

