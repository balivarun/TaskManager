package com.example.TaskManager.config;

import com.example.TaskManager.model.Role;
import com.example.TaskManager.model.User;
import com.example.TaskManager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@taskmanager.com")) {
                User admin = new User();
                admin.setFullName("Default Admin");
                admin.setEmail("admin@taskmanager.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
            if (!userRepository.existsByEmail("member@taskmanager.com")) {
                User member = new User();
                member.setFullName("Default Member");
                member.setEmail("member@taskmanager.com");
                member.setPassword(passwordEncoder.encode("member123"));
                member.setRole(Role.MEMBER);
                userRepository.save(member);
            }
        };
    }
}
