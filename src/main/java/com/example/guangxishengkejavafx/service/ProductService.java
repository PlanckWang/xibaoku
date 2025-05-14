package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Product;
import com.example.guangxishengkejavafx.model.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> findAllProducts() { // Alias for getAllProducts
        return getAllProducts();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductByName(String name) {
        return productRepository.findByProductName(name);
    }

    @Transactional
    public Product saveProduct(Product product) {
        // Check for duplicate product name
        Optional<Product> existingProduct = productRepository.findByProductName(product.getProductName());
        if (existingProduct.isPresent()) {
            throw new RuntimeException("Product with name '" + product.getProductName() + "' already exists.");
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Integer id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Check for duplicate product name, excluding the current product
        Optional<Product> existingProduct = productRepository.findByProductName(productDetails.getProductName());
        if (existingProduct.isPresent() && !existingProduct.get().getProductId().equals(id)) {
            throw new RuntimeException("Another product with name '" + productDetails.getProductName() + "' already exists.");
        }

        product.setProductName(productDetails.getProductName());
        product.setProductCode(productDetails.getProductCode());
        product.setDescription(productDetails.getDescription());
        product.setCategory(productDetails.getCategory());
        product.setUnitPrice(productDetails.getUnitPrice());
        product.setUnit(productDetails.getUnit());
        product.setSpecifications(productDetails.getSpecifications());
        // UpdatedAt will be handled by @PreUpdate in the entity
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        // Add checks for dependencies (e.g., if this product is part of any orders or material lists) before deleting
        productRepository.deleteById(id);
    }
}

