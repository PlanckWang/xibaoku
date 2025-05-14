package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.DirectProductRelease;
import com.example.guangxishengkejavafx.model.entity.Product;
import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.repository.DirectProductReleaseRepository;
import com.example.guangxishengkejavafx.model.repository.ProductRepository;
import com.example.guangxishengkejavafx.model.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DirectProductReleaseService {

    private final DirectProductReleaseRepository directProductReleaseRepository;
    private final ProductRepository productRepository;
    private final PersonnelRepository personnelRepository;

    @Autowired
    public DirectProductReleaseService(DirectProductReleaseRepository directProductReleaseRepository,
                                       ProductRepository productRepository,
                                       PersonnelRepository personnelRepository) {
        this.directProductReleaseRepository = directProductReleaseRepository;
        this.productRepository = productRepository;
        this.personnelRepository = personnelRepository;
    }

    @Transactional(readOnly = true)
    public List<DirectProductRelease> findAllDirectProductReleases() {
        return directProductReleaseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<DirectProductRelease> findDirectProductReleaseById(Integer id) {
        return directProductReleaseRepository.findById(id);
    }

    @Transactional
    public DirectProductRelease saveDirectProductRelease(DirectProductRelease directProductRelease) {
        // Validate Product
        Product product = productRepository.findById(directProductRelease.getProduct().getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + directProductRelease.getProduct().getProductId()));
        directProductRelease.setProduct(product);

        // Validate Operator (Personnel)
        Personnel operator = personnelRepository.findById(directProductRelease.getOperator().getPersonnelId())
            .orElseThrow(() -> new RuntimeException("Operator (Personnel) not found with ID: " + directProductRelease.getOperator().getPersonnelId()));
        directProductRelease.setOperator(operator);

        return directProductReleaseRepository.save(directProductRelease);
    }

    @Transactional
    public void deleteDirectProductReleaseById(Integer id) {
        if (!directProductReleaseRepository.existsById(id)) {
            throw new RuntimeException("DirectProductRelease not found with ID: " + id);
        }
        directProductReleaseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<DirectProductRelease> findByProduct(Product product) {
        return directProductReleaseRepository.findByProduct(product);
    }

    @Transactional(readOnly = true)
    public List<DirectProductRelease> findByBatchNumber(String batchNumber) {
        return directProductReleaseRepository.findByBatchNumber(batchNumber);
    }

    // Add other business-specific methods as needed
}

