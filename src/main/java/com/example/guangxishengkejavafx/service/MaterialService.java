package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Material;
import com.example.guangxishengkejavafx.model.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Transactional(readOnly = true)
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Material> getMaterialById(Integer id) {
        return materialRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Material> getMaterialByName(String name) {
        return materialRepository.findByMaterialName(name);
    }
 
    @Transactional(readOnly = true)
    public Optional<Material> getMaterialByCode(String code) {
        return materialRepository.findByMaterialCode(code);
    }

    @Transactional
    public Material saveMaterial(Material material) {
        // Check for duplicate material name or code before saving
        Optional<Material> existingByName = materialRepository.findByMaterialName(material.getMaterialName());
        if (existingByName.isPresent()) {
            throw new RuntimeException("Material with name '" + material.getMaterialName() + "' already exists.");
        }
        Optional<Material> existingByCode = materialRepository.findByMaterialCode(material.getMaterialCode());
        if (existingByCode.isPresent()) {
            throw new RuntimeException("Material with code '" + material.getMaterialCode() + "' already exists.");
        }
        return materialRepository.save(material);
    }

    @Transactional
    public Material updateMaterial(Integer id, Material materialDetails) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + id));

        // Check for duplicate material name, excluding the current material
        Optional<Material> existingByName = materialRepository.findByMaterialName(materialDetails.getMaterialName());
        if (existingByName.isPresent() && !existingByName.get().getMaterialId().equals(id)) {
            throw new RuntimeException("Another material with name '" + materialDetails.getMaterialName() + "' already exists.");
        }
        // Check for duplicate material code, excluding the current material
        Optional<Material> existingByCode = materialRepository.findByMaterialCode(materialDetails.getMaterialCode());
        if (existingByCode.isPresent() && !existingByCode.get().getMaterialId().equals(id)) {
            throw new RuntimeException("Another material with code '" + materialDetails.getMaterialCode() + "' already exists.");
        }

        material.setMaterialName(materialDetails.getMaterialName());
        material.setMaterialCode(materialDetails.getMaterialCode());
        material.setCategory(materialDetails.getCategory());
        material.setSpecifications(materialDetails.getSpecifications());
        material.setUnit(materialDetails.getUnit());
        material.setUnitPrice(materialDetails.getUnitPrice());
        material.setSupplier(materialDetails.getSupplier());
        material.setStockQuantity(materialDetails.getStockQuantity());
        material.setWarningStockLevel(materialDetails.getWarningStockLevel());
        material.setNotes(materialDetails.getNotes());
        // UpdatedAt will be handled by @PreUpdate in the entity
        return materialRepository.save(material);
    }

    @Transactional
    public void deleteMaterial(Integer id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material not found with id: " + id);
        }
        // Add checks for dependencies (e.g., if this material is used in any records) before deleting
        materialRepository.deleteById(id);
    }
}

