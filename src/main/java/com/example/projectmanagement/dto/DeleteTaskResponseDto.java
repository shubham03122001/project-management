package com.example.projectmanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DeleteTaskResponseDto {
    private String message;
    private UUID taskId;
    private LocalDateTime timestamp;
}
