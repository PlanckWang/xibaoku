package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // For authorities if not using Spring Security roles directly

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Spring Security User expects a list of GrantedAuthority.
        // If your User entity has a simple role string, you might need to convert it.
        // For example, if user.getRole() returns "ADMIN", "USER", etc.
        // You can create a SimpleGrantedAuthority, e.g., Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        // For simplicity, using an empty list of authorities here. This should be expanded based on actual role/permission model.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getActive(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                new ArrayList<>() // authorities - TODO: Map user.getRole() to GrantedAuthority
        );
    }
}

