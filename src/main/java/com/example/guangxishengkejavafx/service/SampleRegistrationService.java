package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Depositor;
import com.example.guangxishengkejavafx.model.entity.Order;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.model.repository.DepositorRepository;
import com.example.guangxishengkejavafx.model.repository.OrderRepository;
import com.example.guangxishengkejavafx.model.repository.SampleRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SampleRegistrationService {

    private final SampleRegistrationRepository sampleRegistrationRepository;
    private final DepositorRepository depositorRepository;
    private final OrderRepository orderRepository; // Optional, as OrderID can be null

    @Autowired
    public SampleRegistrationService(SampleRegistrationRepository sampleRegistrationRepository,
                                     DepositorRepository depositorRepository,
                                     OrderRepository orderRepository) {
        this.sampleRegistrationRepository = sampleRegistrationRepository;
        this.depositorRepository = depositorRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<SampleRegistration> findAllSampleRegistrations() {
        return sampleRegistrationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SampleRegistration> findSampleRegistrationById(Integer id) {
        return sampleRegistrationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<SampleRegistration> findSampleRegistrationBySampleCode(String sampleCode) {
        return sampleRegistrationRepository.findBySampleCode(sampleCode);
    }

    @Transactional(readOnly = true)
    public Optional<SampleRegistration> findBySampleId(String sampleId) { // Alias for findSampleRegistrationBySampleCode
        return findSampleRegistrationBySampleCode(sampleId);
    }

    @Transactional
    public SampleRegistration saveSampleRegistration(SampleRegistration sampleRegistration) {
        // Validate Customer
        Depositor customer = depositorRepository.findById(sampleRegistration.getCustomer().getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer (Depositor) not found with ID: " + sampleRegistration.getCustomer().getCustomerId()));
        sampleRegistration.setCustomer(customer);

        // Validate Order if present
        if (sampleRegistration.getOrder() != null && sampleRegistration.getOrder().getOrderId() != null) {
            Order order = orderRepository.findById(sampleRegistration.getOrder().getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + sampleRegistration.getOrder().getOrderId()));
            sampleRegistration.setOrder(order);
        } else {
            sampleRegistration.setOrder(null); // Ensure it's explicitly null if not provided or ID is null
        }
        
        // Ensure unique sample code before saving
        if (sampleRegistration.getRegistrationId() == null) { // New entity
            sampleRegistrationRepository.findBySampleCode(sampleRegistration.getSampleCode()).ifPresent(existing -> {
                throw new RuntimeException("Sample code already exists: " + sampleRegistration.getSampleCode());
            });
        } else { // Existing entity, check if sample code changed and if new one exists
            SampleRegistration existingRegistration = sampleRegistrationRepository.findById(sampleRegistration.getRegistrationId()).orElse(null);
            if (existingRegistration != null && !existingRegistration.getSampleCode().equals(sampleRegistration.getSampleCode())){
                 sampleRegistrationRepository.findBySampleCode(sampleRegistration.getSampleCode()).ifPresent(existing -> {
                    throw new RuntimeException("Sample code already exists: " + sampleRegistration.getSampleCode());
                });
            }
        }

        return sampleRegistrationRepository.save(sampleRegistration);
    }

    @Transactional
    public void deleteSampleRegistrationById(Integer id) {
        if (!sampleRegistrationRepository.existsById(id)) {
            throw new RuntimeException("SampleRegistration not found with ID: " + id);
        }
        sampleRegistrationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SampleRegistration> findSampleRegistrationsByStatus(String status) {
        return sampleRegistrationRepository.findByStatus(status);
    }

    // Add other business-specific methods as needed
}

