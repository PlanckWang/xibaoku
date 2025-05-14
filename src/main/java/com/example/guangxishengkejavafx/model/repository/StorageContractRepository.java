package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.StorageContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageContractRepository extends JpaRepository<StorageContract, Integer> {
    Optional<StorageContract> findByContractNumber(String contractNumber);
    // Add other custom query methods if needed, for example:
    // List<StorageContract> findByCustomer(Depositor customer);
    // List<StorageContract> findByPaymentStatus(String paymentStatus);
}

