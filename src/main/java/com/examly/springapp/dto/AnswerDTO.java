package com.examly.springapp.dto;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long questionId;
    private Long selectedOptionId;
}
