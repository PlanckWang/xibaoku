package com.example.guangxishengkejavafx.config;

import com.example.guangxishengkejavafx.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity in desktop apps, consider if needed
            .authorizeHttpRequests(authz -> authz
                // Permit all for static resources and potentially initial FXML for login
                // This needs careful configuration based on how JavaFX serves/loads resources
                // For a pure Spring Boot + JavaFX app where Spring doesn't serve HTTP, this might be less relevant
                // or might need to be adjusted if there are any web-exposed endpoints.
                // For now, assuming most control is within JavaFX, permit all as a baseline.
                .anyRequest().permitAll() // Adjust this as per actual security requirements for any web parts
            );
            // If you had a formLogin for a web part, it would be configured here.
            // For JavaFX, login is typically handled by a custom UI calling AuthenticationManager.

        return http.build();
    }
}

