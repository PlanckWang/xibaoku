package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Order;
import com.example.guangxishengkejavafx.model.repository.OrderRepository;
import com.example.guangxishengkejavafx.model.repository.DepositorRepository;
import com.example.guangxishengkejavafx.model.repository.ProductRepository;
import com.example.guangxishengkejavafx.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final DepositorRepository depositorRepository; // Assuming you might need to validate/fetch customer
    private final ProductRepository productRepository;   // Assuming you might need to validate/fetch product
    private final UserRepository userRepository;         // Assuming you might need to validate/fetch salesperson

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        DepositorRepository depositorRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.depositorRepository = depositorRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Transactional
    public Order saveOrder(Order order) {
        // Potential validation or business logic before saving
        // For example, check if customer, product, salesperson exist
        if (order.getCustomer() != null && order.getCustomer().getCustomerId() != null) {
            depositorRepository.findById(order.getCustomer().getCustomerId())
                .orElseThrow(() -> new RuntimeException("Depositor not found with ID: " + order.getCustomer().getCustomerId()));
        }
        if (order.getProduct() != null && order.getProduct().getProductId() != null) {
            productRepository.findById(order.getProduct().getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + order.getProduct().getProductId()));
        }
        if (order.getSalesperson() != null && order.getSalesperson().getUserId() != null) {
            userRepository.findById(order.getSalesperson().getUserId())
                .orElseThrow(() -> new RuntimeException("Salesperson not found with ID: " + order.getSalesperson().getUserId()));
        }
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrderById(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    // Add other business-specific methods as needed
}

