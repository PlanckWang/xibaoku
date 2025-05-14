package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.MaterialReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialReturnRecordRepository extends JpaRepository<MaterialReturnRecord, Integer> {

    List<MaterialReturnRecord> findByMaterialId(Integer materialId);

    List<MaterialReturnRecord> findByOperatorUserId(Integer operatorUserId);
    
    List<MaterialReturnRecord> findByRelatedInboundRecordId(Integer relatedInboundRecordId);

    // Additional query methods can be added here as needed, for example, to find records by date range, reason, etc.
}

