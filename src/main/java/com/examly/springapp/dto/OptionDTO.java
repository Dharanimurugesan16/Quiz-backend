package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OptionDTO {
    private Long id;
    
    @NotBlank(message = "Option text is required")
    private String optionText;

    private Boolean isCorrect=false;
}
