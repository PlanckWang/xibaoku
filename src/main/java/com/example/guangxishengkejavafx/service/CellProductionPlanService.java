package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.CellProductionPlan;
import com.example.guangxishengkejavafx.model.repository.CellProductionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CellProductionPlanService {

    private final CellProductionPlanRepository cellProductionPlanRepository;

    @Autowired
    public CellProductionPlanService(CellProductionPlanRepository cellProductionPlanRepository) {
        this.cellProductionPlanRepository = cellProductionPlanRepository;
    }

    public List<CellProductionPlan> findAll() {
        return cellProductionPlanRepository.findAll();
    }

    public Optional<CellProductionPlan> findById(Integer id) {
        return cellProductionPlanRepository.findById(id);
    }

    public CellProductionPlan save(CellProductionPlan cellProductionPlan) {
        // Add any business logic before saving, e.g., setting default status if not provided
        if (cellProductionPlan.getStatus() == null || cellProductionPlan.getStatus().isEmpty()) {
            cellProductionPlan.setStatus("计划中"); // Default status
        }
        return cellProductionPlanRepository.save(cellProductionPlan);
    }

    public void deleteById(Integer id) {
        cellProductionPlanRepository.deleteById(id);
    }

    public List<CellProductionPlan> findByPlanNameContaining(String planName) {
        return cellProductionPlanRepository.findByPlanNameContainingIgnoreCase(planName);
    }

    public List<CellProductionPlan> findByStatus(String status) {
        return cellProductionPlanRepository.findByStatus(status);
    }

    // Add other business logic methods as needed
}

