package com.examly.springapp.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuizDTO {
    private Long id;

    @NotBlank(message = "title is required")
    @Size(min=3,max=100,message="Title must be between 3 and 100 characters")
    private String title;

    private String description;

    @Max(value=60,message = "time limit must not exceed 60")
    private Integer timeLimit;

    private LocalDateTime createdAt;
    
}
