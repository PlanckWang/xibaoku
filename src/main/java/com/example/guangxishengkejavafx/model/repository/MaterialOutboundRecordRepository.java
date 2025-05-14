package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.MaterialOutboundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialOutboundRecordRepository extends JpaRepository<MaterialOutboundRecord, Integer> {

    List<MaterialOutboundRecord> findByMaterialId(Integer materialId);

    List<MaterialOutboundRecord> findByOperatorUserId(Integer operatorUserId);

    List<MaterialOutboundRecord> findByRecipientDepartmentId(Integer departmentId);

    List<MaterialOutboundRecord> findByRecipientPersonnelId(Integer personnelId);
    
    List<MaterialOutboundRecord> findByRelatedProjectId(Integer projectId);

    // Additional query methods can be added here as needed, for example, to find records by date range, purpose, etc.
}

