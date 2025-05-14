package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.ResourceStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceStatisticRepository extends JpaRepository<ResourceStatistic, Long>, JpaSpecificationExecutor<ResourceStatistic> {
    List<ResourceStatistic> findByNameContainingIgnoreCase(String name);
    List<ResourceStatistic> findByCategory(String category);
}

