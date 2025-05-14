package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.CellProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellProductionPlanRepository extends JpaRepository<CellProductionPlan, Integer> {
    List<CellProductionPlan> findByPlanNameContainingIgnoreCase(String planName);
    List<CellProductionPlan> findByStatus(String status);
    List<CellProductionPlan> findByCreatorId(Integer creatorId);
}

