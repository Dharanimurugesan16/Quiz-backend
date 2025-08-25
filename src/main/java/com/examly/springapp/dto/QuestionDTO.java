package com.examly.springapp.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;

    @NotBlank(message="Question text is required")
    private String questionText;

    @NotBlank(message="Question type is required")
    private String questionType;

    private List<OptionDTO> options;
}
