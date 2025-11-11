package com.example.projectmanagement.service;


import com.example.projectmanagement.dto.CreateProjectRequestDto;
import com.example.projectmanagement.dto.GetAllProjectsResponseDto;
import com.example.projectmanagement.exceptions.ResourceNotFoundException;
import com.example.projectmanagement.exceptions.UnauthorizedAccessException;
import com.example.projectmanagement.model.ProjectEntity;
import com.example.projectmanagement.model.UserEntity;
import com.example.projectmanagement.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    public ProjectEntity createProject(CreateProjectRequestDto createProjectRequestDto, UserEntity user){

        logger.info("Creating project for user:{} ({})",user.getUserName(),user.getEmail());

        ProjectEntity createdProject = createProjectRequestDto.toModel();
        createdProject.setUserEntity(user);
        ProjectEntity savedProject = projectRepository.save(createdProject);
        logger.debug("Project entity before save: {}", createdProject);
        logger.info("Project '{}' created successfully with ID: {}", savedProject.getName(), savedProject.getId());

        return savedProject;


    }


    public List<GetAllProjectsResponseDto> getAllProjects(){
        logger.info("Fetching all projects from database...");
        List<ProjectEntity> projects = projectRepository.findAll();
        if (projects.isEmpty()) {
            logger.warn("No projects found in the database!");
        } else {
            logger.info("Fetched {} projects from the database.", projects.size());
        }
        return  projects.stream()
                .map(project->GetAllProjectsResponseDto.builder()
                        .id(project.getId())
                        .name(project.getName())
                        .description(project.getDescription())
                        .createdAt(project.getCreatedAt())
                        .updatedAt(project.getUpdatedAt()).build())
              .toList();

    };

    public ProjectEntity updateProjectById(UUID id,CreateProjectRequestDto createProjectRequestDto,UserEntity user){
        ProjectEntity existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        existingProject.setName(createProjectRequestDto.getName());
        existingProject.setDescription(createProjectRequestDto.getDescription());
        existingProject.setUserEntity(user);
        existingProject.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(existingProject);

    }

    public void deleteProjectById(UUID id, UserEntity user) {
        ProjectEntity existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        // ✅ (Optional) Ensure the logged-in user owns this project
        if (!existingProject.getUserEntity().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this project");
        }

        logger.info("Deleting project with ID: {}", id);
        projectRepository.delete(existingProject);
        logger.info("Project deleted successfully with ID: {}", id);
    }

}
