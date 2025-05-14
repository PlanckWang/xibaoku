package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.OrderAudit;
import com.example.guangxishengkejavafx.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderAuditRepository extends JpaRepository<OrderAudit, Integer> {
    List<OrderAudit> findByOrder(Order order);
    List<OrderAudit> findByOrderByAuditDateDesc();
}

