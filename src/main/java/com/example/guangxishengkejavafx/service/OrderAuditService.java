package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Order;
import com.example.guangxishengkejavafx.model.entity.OrderAudit;
import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.model.repository.OrderAuditRepository;
import com.example.guangxishengkejavafx.model.repository.OrderRepository;
import com.example.guangxishengkejavafx.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderAuditService {

    private final OrderAuditRepository orderAuditRepository;
    private final OrderRepository orderRepository; // To update order status after audit
    private final UserRepository userRepository; // To validate auditor

    @Autowired
    public OrderAuditService(OrderAuditRepository orderAuditRepository, 
                             OrderRepository orderRepository, 
                             UserRepository userRepository) {
        this.orderAuditRepository = orderAuditRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderAudit> findAllOrderAudits() {
        return orderAuditRepository.findByOrderByAuditDateDesc();
    }

    @Transactional(readOnly = true)
    public Optional<OrderAudit> findOrderAuditById(Integer id) {
        return orderAuditRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderAudit> findAuditsByOrder(Order order) {
        return orderAuditRepository.findByOrder(order);
    }

    @Transactional
    public OrderAudit saveOrderAudit(OrderAudit orderAudit) {
        // Validate Order
        Order order = orderRepository.findById(orderAudit.getOrder().getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderAudit.getOrder().getOrderId()));
        orderAudit.setOrder(order);

        // Validate Auditor
        User auditor = userRepository.findById(orderAudit.getAuditor().getUserId())
            .orElseThrow(() -> new RuntimeException("Auditor (User) not found with ID: " + orderAudit.getAuditor().getUserId()));
        orderAudit.setAuditor(auditor);

        // Save the audit record
        OrderAudit savedAudit = orderAuditRepository.save(orderAudit);

        // Update the order status based on the audit result
        // This is a common pattern, adjust as per specific business logic
        // For example, if auditStatus is "通过", update order.OrderStatus to "已审核"
        // If auditStatus is "驳回", update order.OrderStatus to "审核驳回"
        // This logic might be more complex depending on the workflow
        if ("通过".equals(savedAudit.getAuditStatus())) {
            order.setOrderStatus("已审核");
            orderRepository.save(order);
        } else if ("驳回".equals(savedAudit.getAuditStatus())) {
            order.setOrderStatus("审核驳回"); // Or some other status indicating rejection
            orderRepository.save(order);
        }
        // Potentially add more statuses or logic here

        return savedAudit;
    }

    // Delete operation for audits might not be common, but can be added if needed.
    // @Transactional
    // public void deleteOrderAuditById(Integer id) {
    //     if (!orderAuditRepository.existsById(id)) {
    //         throw new RuntimeException("OrderAudit not found with ID: " + id);
    //     }
    //     orderAuditRepository.deleteById(id);
    // }
}

