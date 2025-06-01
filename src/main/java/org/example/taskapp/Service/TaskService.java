package org.example.taskapp.Service;

import lombok.RequiredArgsConstructor;
import org.example.taskapp.Entity.Task;
import org.example.taskapp.Entity.User;
import org.example.taskapp.Repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService currentUserService;

    public Task createTask(Task task) {
        User currentUser = currentUserService.getCurrentUser();
        task.setUser(currentUser);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(String status) {
        User currentUser = currentUserService.getCurrentUser();
        if (status == null) {
            return taskRepository.findByUserId(currentUser.getId());
        } else {
            return taskRepository.findByUserIdAndStatus(currentUser.getId(), status);
        }
    }

    public Task getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            throw new AccessDeniedException("Not your task!");
        }
        return task;
    }

    public Task updateTask(UUID id, Task updatedTask) {
        Task task = getTaskById(id);
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        return taskRepository.save(task);
    }

    public void deleteTask(UUID id) {
        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Задача не найдена или не принадлежит пользователю"));

        taskRepository.delete(task);
    }
}
