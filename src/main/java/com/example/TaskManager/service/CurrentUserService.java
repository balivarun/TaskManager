package com.example.TaskManager.service;

import com.example.TaskManager.model.User;
import com.example.TaskManager.repository.UserRepository;
import com.example.TaskManager.security.AppUserPrincipal;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User fromPrincipal(AppUserPrincipal principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new com.example.TaskManager.exception.NotFoundException("Current user not found"));
    }
}
