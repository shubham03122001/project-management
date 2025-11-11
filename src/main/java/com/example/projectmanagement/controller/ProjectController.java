package com.example.projectmanagement.controller;


import com.example.projectmanagement.dto.CreateProjectRequestDto;
import com.example.projectmanagement.dto.GetAllProjectsResponseDto;
import com.example.projectmanagement.dto.ProjectResponseDto;
import com.example.projectmanagement.dto.ProjectUpdateResponseDto;
import com.example.projectmanagement.model.ProjectEntity;
import com.example.projectmanagement.model.UserEntity;
import com.example.projectmanagement.repository.UserRepository;
import com.example.projectmanagement.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody CreateProjectRequestDto createProjectRequestDto){
        //Get the logged in user from the JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));


        ProjectEntity createdProject = projectService.createProject(createProjectRequestDto, user);

        ProjectResponseDto projectResponseDto = new ProjectResponseDto(
                createdProject.getId(),
                createdProject.getName(),
                createdProject.getDescription(),
                createdProject.getCreatedAt()

        );


        return new ResponseEntity<>(projectResponseDto, HttpStatus.CREATED);

    }


    @GetMapping("/getAllProjects")
    public ResponseEntity<?> getAllProjects(){
        List<GetAllProjectsResponseDto> projects = projectService.getAllProjects();

        return new ResponseEntity<>(projects,HttpStatus.OK);

    }

    @PutMapping("/updateProjectByID/{id}")
    public ResponseEntity<ProjectUpdateResponseDto> updateProjectById(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProjectRequestDto createProjectRequestDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectEntity updatedProject = projectService.updateProjectById(id, createProjectRequestDto, user);

        ProjectUpdateResponseDto response = ProjectUpdateResponseDto.builder()
                .id(updatedProject.getId())
                .name(updatedProject.getName())
                .description(updatedProject.getDescription())
                .createdAt(updatedProject.getCreatedAt())
                .updatedAt(updatedProject.getUpdatedAt())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/deleteProjectByID/{id}")
    public ResponseEntity<?> deleteProjectById(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        projectService.deleteProjectById(id, user);

        return ResponseEntity.ok(Map.of(
                "message", "Project deleted successfully",
                "projectId", id
        ));
    }

}
