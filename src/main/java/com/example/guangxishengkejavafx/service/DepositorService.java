package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Depositor;
import com.example.guangxishengkejavafx.model.repository.DepositorRepository;
import com.example.guangxishengkejavafx.model.repository.UserRepository; // For registeredByUser validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepositorService {

    private final DepositorRepository depositorRepository;
    private final UserRepository userRepository; // To validate the user who registers the depositor

    @Autowired
    public DepositorService(DepositorRepository depositorRepository, UserRepository userRepository) {
        this.depositorRepository = depositorRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Depositor> getAllDepositors() {
        return depositorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Depositor> findAllDepositors() { // Alias for getAllDepositors
        return getAllDepositors();
    }

    @Transactional(readOnly = true)
    public Optional<Depositor> getDepositorById(Integer id) {
        return depositorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Depositor> getDepositorByIdCardNumber(String idCardNumber) {
        return depositorRepository.findByIdCardNumber(idCardNumber);
    }

    @Transactional(readOnly = true)
    public List<Depositor> findDepositorsByName(String name) {
        return depositorRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Depositor saveDepositor(Depositor depositor) {
        // Validate registeredByUser if provided
        if (depositor.getRegisteredByUserId() != null && !userRepository.existsById(depositor.getRegisteredByUserId())) {
            throw new RuntimeException("Registered by user not found with id: " + depositor.getRegisteredByUserId());
        }
        // Check for duplicate ID card number before saving a new depositor or updating an existing one
        Optional<Depositor> existingDepositorByIdCard = depositorRepository.findByIdCardNumber(depositor.getIdCardNumber());
        if (existingDepositorByIdCard.isPresent() && (depositor.getDepositorId() == null || !existingDepositorByIdCard.get().getDepositorId().equals(depositor.getDepositorId()))) {
            throw new RuntimeException("Depositor with ID card number " + depositor.getIdCardNumber() + " already exists.");
        }
        return depositorRepository.save(depositor);
    }

    @Transactional
    public Depositor updateDepositor(Integer id, Depositor depositorDetails) {
        Depositor depositor = depositorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Depositor not found with id: " + id));

        // Check for duplicate ID card number if it's being changed
        if (!depositor.getIdCardNumber().equals(depositorDetails.getIdCardNumber())) {
            Optional<Depositor> existingDepositorByIdCard = depositorRepository.findByIdCardNumber(depositorDetails.getIdCardNumber());
            if (existingDepositorByIdCard.isPresent()) {
                throw new RuntimeException("Another depositor with ID card number " + depositorDetails.getIdCardNumber() + " already exists.");
            }
        }
        
        // Validate registeredByUser if provided and changed
        if (depositorDetails.getRegisteredByUserId() != null && 
            (depositor.getRegisteredByUserId() == null || !depositor.getRegisteredByUserId().equals(depositorDetails.getRegisteredByUserId()))) {
            if (!userRepository.existsById(depositorDetails.getRegisteredByUserId())) {
                 throw new RuntimeException("Registered by user not found with id: " + depositorDetails.getRegisteredByUserId());
            }
            depositor.setRegisteredByUserId(depositorDetails.getRegisteredByUserId());
        } else if (depositorDetails.getRegisteredByUserId() == null) {
            depositor.setRegisteredByUserId(null); // Allow clearing registered user
        }

        depositor.setName(depositorDetails.getName());
        depositor.setGender(depositorDetails.getGender());
        depositor.setDateOfBirth(depositorDetails.getDateOfBirth());
        depositor.setIdCardNumber(depositorDetails.getIdCardNumber());
        depositor.setPhoneNumber(depositorDetails.getPhoneNumber());
        depositor.setAddress(depositorDetails.getAddress());
        depositor.setEmail(depositorDetails.getEmail());
        depositor.setOccupation(depositorDetails.getOccupation());
        depositor.setEmergencyContactName(depositorDetails.getEmergencyContactName());
        depositor.setEmergencyContactPhone(depositorDetails.getEmergencyContactPhone());
        depositor.setRegistrationDate(depositorDetails.getRegistrationDate()); // Allow updating registration date
        depositor.setNotes(depositorDetails.getNotes());
        // registeredByUserId is handled above

        return depositorRepository.save(depositor);
    }

    @Transactional
    public void deleteDepositor(Integer id) {
        if (!depositorRepository.existsById(id)) {
            throw new RuntimeException("Depositor not found with id: " + id);
        }
        // Add any pre-delete checks here, e.g., if depositor has active orders or contracts
        depositorRepository.deleteById(id);
    }
}

