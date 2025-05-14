package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Integer> {

    Optional<Personnel> findByUserId(Integer userId);

    Optional<Personnel> findByEmployeeId(String employeeId);

    // Add other custom query methods if needed
}

