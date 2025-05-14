package com.example.guangxishengkejavafx.model.repository;

import com.example.guangxishengkejavafx.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Add other custom query methods if needed, for example:
    // List<User> findByRole(String role);
    // List<User> findByInstitutionId(Integer institutionId);
}

