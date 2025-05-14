package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.CellBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellBankRepository extends JpaRepository<CellBank, Integer> {
    List<CellBank> findByBankType(String bankType);
}

