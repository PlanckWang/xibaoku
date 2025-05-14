package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    Optional<Material> findByMaterialName(String materialName);

    Optional<Material> findByMaterialCode(String materialCode);

    // Add other custom query methods if needed, for example:
    // List<Material> findBySupplier(String supplier);
    // List<Material> findByCurrentStockLessThan(Integer stockLevel);
}

