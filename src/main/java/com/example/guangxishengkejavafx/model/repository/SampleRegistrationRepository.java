package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SampleRegistrationRepository extends JpaRepository<SampleRegistration, Integer> {
    Optional<SampleRegistration> findBySampleCode(String sampleCode);
    List<SampleRegistration> findByStatus(String status);
    // Add other custom query methods if needed, for example:
    // List<SampleRegistration> findByCustomer(Depositor customer);
    // List<SampleRegistration> findByOrder(Order order);
}

