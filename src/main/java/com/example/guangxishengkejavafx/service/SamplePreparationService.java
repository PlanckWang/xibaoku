package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.Product;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.model.repository.PersonnelRepository;
import com.example.guangxishengkejavafx.model.repository.ProductRepository;
import com.example.guangxishengkejavafx.model.repository.SamplePreparationRepository;
import com.example.guangxishengkejavafx.model.repository.SampleRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SamplePreparationService {

    private final SamplePreparationRepository samplePreparationRepository;
    private final SampleRegistrationRepository sampleRegistrationRepository;
    private final ProductRepository productRepository;
    private final PersonnelRepository personnelRepository;

    @Autowired
    public SamplePreparationService(SamplePreparationRepository samplePreparationRepository,
                                    SampleRegistrationRepository sampleRegistrationRepository,
                                    ProductRepository productRepository,
                                    PersonnelRepository personnelRepository) {
        this.samplePreparationRepository = samplePreparationRepository;
        this.sampleRegistrationRepository = sampleRegistrationRepository;
        this.productRepository = productRepository;
        this.personnelRepository = personnelRepository;
    }

    @Transactional(readOnly = true)
    public List<SamplePreparation> findAllSamplePreparations() {
        return samplePreparationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SamplePreparation> findSamplePreparationById(Integer id) {
        return samplePreparationRepository.findById(id);
    }

    @Transactional
    public SamplePreparation saveSamplePreparation(SamplePreparation samplePreparation) {
        // Validate SampleRegistration
        SampleRegistration registration = sampleRegistrationRepository.findById(samplePreparation.getSampleRegistration().getRegistrationId())
            .orElseThrow(() -> new RuntimeException("SampleRegistration not found with ID: " + samplePreparation.getSampleRegistration().getRegistrationId()));
        samplePreparation.setSampleRegistration(registration);

        // Validate Product
        if (samplePreparation.getProduct() != null && samplePreparation.getProduct().getProductId() != null) {
            Product product = productRepository.findById(samplePreparation.getProduct().getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + samplePreparation.getProduct().getProductId()));
            samplePreparation.setProduct(product);
        } else {
            samplePreparation.setProduct(null); // Allow null product if business logic permits
        }

        // Validate Operator (Personnel)
        if (samplePreparation.getOperator() != null && samplePreparation.getOperator().getPersonnelId() != null) {
            Personnel operator = personnelRepository.findById(samplePreparation.getOperator().getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Operator (Personnel) not found with ID: " + samplePreparation.getOperator().getPersonnelId()));
            samplePreparation.setOperator(operator);
        } else {
            samplePreparation.setOperator(null); // Allow null operator if business logic permits
        }

        return samplePreparationRepository.save(samplePreparation);
    }

    @Transactional
    public void deleteSamplePreparationById(Integer id) {
        if (!samplePreparationRepository.existsById(id)) {
            throw new RuntimeException("SamplePreparation not found with ID: " + id);
        }
        samplePreparationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SamplePreparation> findByStatus(String status) {
        return samplePreparationRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<SamplePreparation> findByBatchNumber(String batchNumber) {
        return samplePreparationRepository.findByBatchNumber(batchNumber);
    }

    // Add other business-specific methods as needed
}

