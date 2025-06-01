package org.example.taskapp.Repository;

import org.example.taskapp.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByUserId(UUID userId);
    List<Task> findByUserIdAndStatus(UUID userId, String status);
    Optional<Task> findByIdAndUserId(UUID taskId, UUID userId);
}
