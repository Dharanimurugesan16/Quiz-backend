package com.examly.springapp.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class QuizAttemptDTO {
    private Long quizId;
    private String studentName;
    private int score;
    private int totalQuestions;
    private LocalDateTime completedAt;
    private List<AnswerDTO> answers;
}
