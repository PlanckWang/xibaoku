package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.Depositor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepositorRepository extends JpaRepository<Depositor, Integer> {

    Optional<Depositor> findByIdCardNumber(String idCardNumber);

    List<Depositor> findByNameContainingIgnoreCase(String name);

    List<Depositor> findByPhoneNumber(String phoneNumber);

    // Additional query methods can be added here as needed
}

