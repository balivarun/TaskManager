package com.example.TaskManager.repository;

import com.example.TaskManager.model.Project;
import com.example.TaskManager.model.Task;
import com.example.TaskManager.model.TaskStatus;
import com.example.TaskManager.model.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectOrderByCreatedAtDesc(Project project);
    List<Task> findByProjectInOrderByCreatedAtDesc(List<Project> projects);
    List<Task> findByAssigneeOrderByDueDateAsc(User assignee);
    long countByAssignee(User assignee);
    long countByAssigneeAndStatus(User assignee, TaskStatus status);
    long countByAssigneeAndDueDateBeforeAndStatusNot(User assignee, LocalDate date, TaskStatus status);
    long countByStatus(TaskStatus status);
    long countByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);
}
