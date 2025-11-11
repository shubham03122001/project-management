package com.example.projectmanagement.service;


import com.example.projectmanagement.dto.CreateTaskRequestDto;
import com.example.projectmanagement.dto.TaskResponseDto;
import com.example.projectmanagement.enums.Priority;
import com.example.projectmanagement.enums.Status;
import com.example.projectmanagement.exceptions.ResourceNotFoundException;
import com.example.projectmanagement.exceptions.UnauthorizedAccessException;
import com.example.projectmanagement.model.ProjectEntity;
import com.example.projectmanagement.model.TaskEntity;
import com.example.projectmanagement.model.UserEntity;
import com.example.projectmanagement.repository.ProjectRepository;
import com.example.projectmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);



    public TaskResponseDto createTask(UUID projectId, CreateTaskRequestDto request, UserEntity user) {
        logger.info("Attempting to create task '{}' under project ID: {}", request.getTitle(), projectId);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    logger.error("Project not found with ID: {}", projectId);
                    return new ResourceNotFoundException("Project not found with ID: " + projectId);
                });

        // Ensure logged-in user owns this project
        if (!project.getUserEntity().getId().equals(user.getId())) {
            logger.warn("User {} tried to create task in unauthorized project {}", user.getEmail(), projectId);
            throw new UnauthorizedAccessException("You are not authorized to add tasks to this project");
        }

        TaskEntity task = request.toEntity();
        task.setProject(project);
        task.setUser(user);
        TaskEntity saved = taskRepository.save(task);
        logger.info("Task '{}' created successfully with ID: {}", task.getTitle(), task.getId());
        return mapToResponse(saved);
    }
    public TaskResponseDto updateTask(UUID taskId, CreateTaskRequestDto request, UserEntity user) {
        logger.info("Updating task with ID: {}", taskId);
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        task.setUpdatedAt(LocalDateTime.now());

        TaskEntity updatedTask = taskRepository.save(task);
        logger.info("Task '{}' updated successfully with ID: {}", updatedTask.getTitle(), updatedTask.getId());
        return mapToResponse(updatedTask);
    }
    public List<TaskResponseDto> listTasks(UserEntity user, Status status, Priority priority, String sortBy) {
        List<TaskEntity> tasks = taskRepository.findByUser(user);

        if (status != null) tasks = tasks.stream().filter(t -> t.getStatus() == status).toList();
        if (priority != null) tasks = tasks.stream().filter(t -> t.getPriority() == priority).toList();

        // Sorting
        if ("dueDate".equalsIgnoreCase(sortBy))
            tasks = tasks.stream().sorted(Comparator.comparing(TaskEntity::getDueDate)).toList();
        else if ("priority".equalsIgnoreCase(sortBy))
            tasks = tasks.stream().sorted(Comparator.comparing(TaskEntity::getPriority)).toList();

        return tasks.stream().map(this::mapToResponse).toList();
    }

    public List<TaskResponseDto> searchTasks(UserEntity user, String keyword) {
        logger.info("Fetching all tasks for user: {}", user.getEmail());
        return taskRepository.searchTasks(user, keyword).stream()
                .map(this::mapToResponse)
                .toList();
    }
    public void deleteTask(UUID id, UserEntity user) {
        logger.info("Attempting to delete task with ID: {}", id);
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        // Ensure user owns the task's project
        if (!task.getUser().getId().equals(user.getId())) {
            logger.warn("User {} tried to delete unauthorized task {}", user.getEmail(), id);
            throw new RuntimeException("You are not authorized to delete this task");
        }

        taskRepository.delete(task);
        logger.info("Task deleted successfully with ID: {}", id);
    }
    private TaskResponseDto mapToResponse(TaskEntity entity) {
        return TaskResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .dueDate(entity.getDueDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


}
