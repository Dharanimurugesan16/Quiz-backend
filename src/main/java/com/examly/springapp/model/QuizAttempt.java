package com.examly.springapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @Column(name = "quiz_id")
    @NotNull
    private Long quizId;

    @Column(name = "user_id")
    @NotNull
    private String userId; // Adjust to Long if your User model uses a numeric ID

    @Column(name = "answers")
    @JdbcTypeCode(SqlTypes.JSON)
    @NotNull
    private Map<String, AnswerDetails> answers;

    @Column(name = "score")
    @NotNull
    private Integer score;

    @Column(name = "total_questions")
    @NotNull
    private Integer totalQuestions;

    @Column(name = "time_spent")
    @NotNull
    private Integer timeSpent;

    @Column(name = "completed_date")
    @NotNull
    private LocalDateTime completedDate;

    @Builder.Default
    @Column(name = "attempts")
    private Integer attempts = 1;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDetails {
        private String selectedAnswer;
        private String correctAnswer;
    }
}