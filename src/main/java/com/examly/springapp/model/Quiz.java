package com.examly.springapp.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private long id;

    @Column(name = "title")
    @NotBlank
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "time_limit")
    private int timeLimit;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deadline")
    @NotNull
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    @NotNull
    private Difficulty difficulty;

    @Column(name = "category") // Added field
    private String category; // Adjust type if needed (e.g., Enum)

    @ManyToMany
    @JoinTable(
            name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @JsonIgnoreProperties("quizzes")
    private List<Question> questions = new ArrayList<>();

    @ManyToMany(mappedBy = "assignedQuizzes")
    @JsonIgnoreProperties("assignedQuizzes")
    @Builder.Default
    private List<User> users = new ArrayList<>();

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}