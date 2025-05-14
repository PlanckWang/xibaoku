package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getAllActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::getActive) // Assuming User entity has an isActive() or getActive() method
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // Example method - replace with actual methods needed by DepositorController or other classes
    public boolean authenticate(String username, String password) {
        // Placeholder authentication logic, real logic would involve passwordEncoder and DB check
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            // This is a simplified check. Real check would use passwordEncoder.matches()
            // return passwordEncoder.matches(password, userOptional.get().getPasswordHash());
            return userOptional.get().getPasswordHash().equals(password); // Placeholder, not secure
        }
        return false;
    }

    public User getCurrentUser() {
        // Placeholder for getting current user details from Spring Security context or similar
        // For now, returning a dummy user or null
        // Example: return userRepository.findByUsername("currentUserUsername").orElse(null);
        return null;
    }

    // Add other methods as required by the application
    // For example, methods to manage users, roles, permissions etc.
    // Methods for creating, updating, deleting users, assigning roles etc.
}

