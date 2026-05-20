package com.example.TaskManager.service;

import com.example.TaskManager.dto.UserSummary;
import com.example.TaskManager.model.User;
import com.example.TaskManager.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummary> findAll() {
        return userRepository.findAll().stream().map(this::toSummary).toList();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new com.example.TaskManager.exception.NotFoundException("User not found"));
    }

    public UserSummary toSummary(User user) {
        return new UserSummary(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
