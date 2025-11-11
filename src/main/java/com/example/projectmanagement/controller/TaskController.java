package com.example.projectmanagement.controller;


import com.example.projectmanagement.dto.CreateTaskRequestDto;
import com.example.projectmanagement.dto.DeleteTaskResponseDto;
import com.example.projectmanagement.dto.TaskResponseDto;
import com.example.projectmanagement.enums.Priority;
import com.example.projectmanagement.enums.Status;
import com.example.projectmanagement.model.UserEntity;
import com.example.projectmanagement.repository.UserRepository;
import com.example.projectmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final UserRepository userRepository;

    private UserEntity getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskResponseDto> createTask(
            @PathVariable UUID projectId,
            @RequestBody CreateTaskRequestDto request) {

        UserEntity user = getLoggedInUser();
        return new ResponseEntity<>(taskService.createTask(projectId, request, user), HttpStatus.CREATED);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable UUID taskId,
            @RequestBody CreateTaskRequestDto request) {

        UserEntity user = getLoggedInUser();
        return ResponseEntity.ok(taskService.updateTask(taskId, request, user));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> listTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String sortBy) {

        UserEntity user = getLoggedInUser();
        return ResponseEntity.ok(taskService.listTasks(user, status, priority, sortBy));
    }
    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDto>> searchTasks(@RequestParam String keyword) {
        UserEntity user = getLoggedInUser();
        return ResponseEntity.ok(taskService.searchTasks(user, keyword));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID taskId) {
        UserEntity user = getLoggedInUser();
        taskService.deleteTask(taskId, user);

        DeleteTaskResponseDto response = new DeleteTaskResponseDto(
                "Task deleted successfully",
                taskId,
                LocalDateTime.now()
        );
       return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
