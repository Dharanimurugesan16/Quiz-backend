package com.examly.springapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "quiz_retake_requests")
public class QuizRetakeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "user_id")
    @NotNull
    private Long userId; // Changed to Long to match User.id

    @Column(name = "quiz_id")
    @NotNull
    private Long quizId;

    @Column(name = "student_username")
    @NotNull
    private String studentUsername;

    @Column(name = "quiz_title")
    @NotNull
    private String quizTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private Status status;

    @Column(name = "request_date")
    @NotNull
    private LocalDateTime requestDate;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @PrePersist
    protected void onCreate() {
        this.requestDate = LocalDateTime.now();
    }
}