package com.example.projectmanagement.dto;

import com.example.projectmanagement.model.TaskEntity;
import lombok.*;
import com.example.projectmanagement.enums.Priority;
import com.example.projectmanagement.enums.Status;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequestDto {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDate dueDate;

    public TaskEntity toEntity() {
        return TaskEntity.builder()
                .title(title)
                .description(description)
                .status(status)
                .priority(priority)
                .dueDate(dueDate)
                .build();
    }

}
