package com.example.projectmanagement.dto;

import com.example.projectmanagement.model.ProjectEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateProjectRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

  public ProjectEntity toModel(){
        return ProjectEntity.builder()
                .name(this.name)
                .description(this.description)
                .build();
    }

}
