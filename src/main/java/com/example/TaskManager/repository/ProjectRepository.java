package com.example.TaskManager.repository;

import com.example.TaskManager.model.Project;
import com.example.TaskManager.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findDistinctByMembersContainingOrderByCreatedAtDesc(User user);
    List<Project> findAllByOrderByCreatedAtDesc();
}
