package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.ResourceStatistic;
import com.example.guangxishengkejavafx.model.repository.ResourceStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceStatisticService {

    private final ResourceStatisticRepository resourceStatisticRepository;

    // Assuming other repositories might be needed to calculate statistics
    // For example: MaterialRepository, FrozenSampleRepository, etc.
    // @Autowired
    // private MaterialRepository materialRepository;
    // @Autowired
    // private FrozenSampleRepository frozenSampleRepository;

    @Autowired
    public ResourceStatisticService(ResourceStatisticRepository resourceStatisticRepository) {
        this.resourceStatisticRepository = resourceStatisticRepository;
    }

    public List<ResourceStatistic> findAll() {
        return resourceStatisticRepository.findAll();
    }

    public Optional<ResourceStatistic> findById(Long id) {
        return resourceStatisticRepository.findById(id);
    }

    public List<ResourceStatistic> findByNameContaining(String name) {
        return resourceStatisticRepository.findByNameContainingIgnoreCase(name);
    }

    public List<ResourceStatistic> findByCategory(String category) {
        return resourceStatisticRepository.findByCategory(category);
    }

    @Transactional
    public ResourceStatistic save(ResourceStatistic resourceStatistic) throws Exception {
        if (resourceStatistic.getName() == null || resourceStatistic.getName().trim().isEmpty()) {
            throw new Exception("统计名称不能为空。");
        }
        // For actual statistics, the value would likely be calculated, not directly set.
        // This save method is more for managing the definitions of what to track.
        resourceStatistic.setLastCalculatedAt(LocalDateTime.now()); // Or when it was actually calculated
        return resourceStatisticRepository.save(resourceStatistic);
    }

    @Transactional
    public void deleteById(Long id) throws Exception {
        if (!resourceStatisticRepository.existsById(id)) {
            throw new Exception("无法找到ID为 " + id + " 的资源统计项进行删除。");
        }
        resourceStatisticRepository.deleteById(id);
    }

    public List<ResourceStatistic> findAll(Specification<ResourceStatistic> spec) {
        return resourceStatisticRepository.findAll(spec);
    }

    /**
     * Placeholder method to simulate calculating/refreshing a specific statistic.
     * In a real application, this would involve querying other tables and performing calculations.
     */
    @Transactional
    public Optional<ResourceStatistic> calculateAndRefreshStatistic(String statisticName) {
        // Example: Find a statistic definition by name
        List<ResourceStatistic> stats = resourceStatisticRepository.findByNameContainingIgnoreCase(statisticName);
        if (stats.isEmpty()) {
            return Optional.empty();
        }
        ResourceStatistic statToUpdate = stats.get(0); // Assuming unique names for simplicity

        // Simulate calculation based on category
        String newValue = "N/A";
        if ("物料".equals(statToUpdate.getCategory())) {
            // long materialCount = materialRepository.count(); // Example
            // newValue = String.valueOf(materialCount);
            newValue = "150"; // Placeholder
        } else if ("冻存样本".equals(statToUpdate.getCategory())) {
            // long frozenSampleCount = frozenSampleRepository.countByStatus("已入库"); // Example
            // newValue = String.valueOf(frozenSampleCount);
            newValue = "2500"; // Placeholder
        }
        // Add more categories and actual calculation logic here

        statToUpdate.setValue(newValue);
        statToUpdate.setLastCalculatedAt(LocalDateTime.now());
        return Optional.of(resourceStatisticRepository.save(statToUpdate));
    }

    /**
     * Method to refresh all defined statistics or specific categories.
     * This would iterate through defined statistics and call calculation logic.
     */
    @Transactional
    public void refreshAllStatistics() {
        List<ResourceStatistic> allStats = resourceStatisticRepository.findAll();
        for (ResourceStatistic stat : allStats) {
            // This is a simplified call, real logic would be more complex
            calculateAndRefreshStatistic(stat.getName());
        }
    }
}

