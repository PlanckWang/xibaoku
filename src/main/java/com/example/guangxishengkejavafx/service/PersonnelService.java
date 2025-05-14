package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.model.repository.DepartmentRepository;
import com.example.guangxishengkejavafx.model.repository.InstitutionRepository;
import com.example.guangxishengkejavafx.model.repository.PersonnelRepository;
import com.example.guangxishengkejavafx.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository; // For validation
    private final DepartmentRepository departmentRepository; // For validation
    private final PasswordEncoder passwordEncoder; // For creating/updating user passwords

    @Autowired
    public PersonnelService(PersonnelRepository personnelRepository, 
                          UserRepository userRepository, 
                          InstitutionRepository institutionRepository,
                          DepartmentRepository departmentRepository,
                          PasswordEncoder passwordEncoder) {
        this.personnelRepository = personnelRepository;
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Personnel> getAllPersonnel() {
        return personnelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Personnel> findAllPersonnel() { // Added to match controller calls
        return getAllPersonnel();
    }

    @Transactional(readOnly = true)
    public Optional<Personnel> getPersonnelById(Integer id) {
        return personnelRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Personnel> getPersonnelByUserId(Integer userId) {
        return personnelRepository.findByUserId(userId);
    }

    @Transactional
    public Personnel savePersonnel(User userDetails, Personnel personnelDetails) {
        // 1. Validate Institution and Department for the User
        if (userDetails.getInstitutionId() != null && !institutionRepository.existsById(userDetails.getInstitutionId())) {
            throw new RuntimeException("Institution not found with id: " + userDetails.getInstitutionId());
        }
        if (userDetails.getDepartmentId() != null && !departmentRepository.existsById(userDetails.getDepartmentId())) {
            throw new RuntimeException("Department not found with id: " + userDetails.getDepartmentId());
        }

        // 2. Check for existing username or email for the User
        if (userRepository.findByUsername(userDetails.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + userDetails.getUsername());
        }
        if (userDetails.getEmail() != null && userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + userDetails.getEmail());
        }

        // 3. Hash password for the new User
        userDetails.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash())); // Assuming raw password is set initially
        User savedUser = userRepository.save(userDetails);

        // 4. Save Personnel linked to the new User
        personnelDetails.setUserId(savedUser.getUserId());
        if (personnelDetails.getEmployeeId() != null && personnelRepository.findByEmployeeId(personnelDetails.getEmployeeId()).isPresent()){
            throw new RuntimeException("Employee ID already exists: " + personnelDetails.getEmployeeId());
        }
        return personnelRepository.save(personnelDetails);
    }

    @Transactional
    public Personnel updatePersonnel(Integer personnelId, User userUpdates, Personnel personnelUpdates) {
        Personnel existingPersonnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found with id: " + personnelId));
        User existingUser = userRepository.findById(existingPersonnel.getUserId())
                .orElseThrow(() -> new RuntimeException("Associated user not found with id: " + existingPersonnel.getUserId()));

        // Update User details
        // Validate Institution and Department if changed
        if (userUpdates.getInstitutionId() != null && !userUpdates.getInstitutionId().equals(existingUser.getInstitutionId())){
            if(!institutionRepository.existsById(userUpdates.getInstitutionId())){
                 throw new RuntimeException("New Institution not found with id: " + userUpdates.getInstitutionId());
            }
            existingUser.setInstitutionId(userUpdates.getInstitutionId());
        }
        if (userUpdates.getDepartmentId() != null && !userUpdates.getDepartmentId().equals(existingUser.getDepartmentId())){
             if(!departmentRepository.existsById(userUpdates.getDepartmentId())){
                 throw new RuntimeException("New Department not found with id: " + userUpdates.getDepartmentId());
            }
            existingUser.setDepartmentId(userUpdates.getDepartmentId());
        }

        if (userUpdates.getUsername() != null && !userUpdates.getUsername().equals(existingUser.getUsername())) {
            userRepository.findByUsername(userUpdates.getUsername()).ifPresent(u -> {
                if (!u.getUserId().equals(existingUser.getUserId())) throw new RuntimeException("Username already taken: " + userUpdates.getUsername());
            });
            existingUser.setUsername(userUpdates.getUsername());
        }
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equals(existingUser.getEmail())) {
            userRepository.findByEmail(userUpdates.getEmail()).ifPresent(u -> {
                if (!u.getUserId().equals(existingUser.getUserId())) throw new RuntimeException("Email already taken: " + userUpdates.getEmail());
            });
            existingUser.setEmail(userUpdates.getEmail());
        }
        if (userUpdates.getPasswordHash() != null && !userUpdates.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userUpdates.getPasswordHash()));
        }
        existingUser.setFullName(userUpdates.getFullName());
        existingUser.setPhoneNumber(userUpdates.getPhoneNumber());
        existingUser.setRole(userUpdates.getRole());
        existingUser.setActive(userUpdates.getActive());
        userRepository.save(existingUser);

        // Update Personnel details
        if (personnelUpdates.getEmployeeId() != null && !personnelUpdates.getEmployeeId().equals(existingPersonnel.getEmployeeId())){
            personnelRepository.findByEmployeeId(personnelUpdates.getEmployeeId()).ifPresent(p -> {
                if(!p.getPersonnelId().equals(existingPersonnel.getPersonnelId())) throw new RuntimeException("Employee ID already taken: " + personnelUpdates.getEmployeeId());
            });
            existingPersonnel.setEmployeeId(personnelUpdates.getEmployeeId());
        }
        existingPersonnel.setPosition(personnelUpdates.getPosition());
        existingPersonnel.setHireDate(personnelUpdates.getHireDate());
        existingPersonnel.setNotes(personnelUpdates.getNotes());
        return personnelRepository.save(existingPersonnel);
    }

    @Transactional
    public void deletePersonnel(Integer personnelId) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found with id: " + personnelId));
        // Also delete the associated user, or handle as per business logic (e.g., deactivate user)
        // For now, deleting both. Consider cascading deletes or soft deletes.
        userRepository.deleteById(personnel.getUserId());
        personnelRepository.deleteById(personnelId);
    }
}

