package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Depositor;
import com.example.guangxishengkejavafx.model.entity.Order;
import com.example.guangxishengkejavafx.model.entity.StorageContract;
import com.example.guangxishengkejavafx.model.repository.DepositorRepository;
import com.example.guangxishengkejavafx.model.repository.OrderRepository;
import com.example.guangxishengkejavafx.model.repository.StorageContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StorageContractService {

    private final StorageContractRepository storageContractRepository;
    private final DepositorRepository depositorRepository;
    private final OrderRepository orderRepository; // Optional, as OrderID can be null

    @Autowired
    public StorageContractService(StorageContractRepository storageContractRepository,
                                DepositorRepository depositorRepository,
                                OrderRepository orderRepository) {
        this.storageContractRepository = storageContractRepository;
        this.depositorRepository = depositorRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<StorageContract> findAllStorageContracts() {
        return storageContractRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StorageContract> findStorageContractById(Integer id) {
        return storageContractRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<StorageContract> findStorageContractByContractNumber(String contractNumber) {
        return storageContractRepository.findByContractNumber(contractNumber);
    }

    @Transactional
    public StorageContract saveStorageContract(StorageContract storageContract) {
        // Validate Customer
        Depositor customer = depositorRepository.findById(storageContract.getCustomer().getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer (Depositor) not found with ID: " + storageContract.getCustomer().getCustomerId()));
        storageContract.setCustomer(customer);

        // Validate Order if present
        if (storageContract.getOrder() != null && storageContract.getOrder().getOrderId() != null) {
            Order order = orderRepository.findById(storageContract.getOrder().getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + storageContract.getOrder().getOrderId()));
            storageContract.setOrder(order);
        } else {
            storageContract.setOrder(null); // Ensure it's explicitly null if not provided or ID is null
        }

        return storageContractRepository.save(storageContract);
    }

    @Transactional
    public void deleteStorageContractById(Integer id) {
        if (!storageContractRepository.existsById(id)) {
            throw new RuntimeException("StorageContract not found with ID: " + id);
        }
        storageContractRepository.deleteById(id);
    }

    // Add other business-specific methods as needed, e.g., for searching by customer, status, etc.
}

