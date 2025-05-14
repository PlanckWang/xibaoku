package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.MaterialInboundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialInboundRecordRepository extends JpaRepository<MaterialInboundRecord, Integer> {

    List<MaterialInboundRecord> findByMaterialId(Integer materialId);

    List<MaterialInboundRecord> findByOperatorUserId(Integer operatorUserId);

    // Additional query methods can be added here as needed, for example, to find records by date range, supplier, etc.
}

