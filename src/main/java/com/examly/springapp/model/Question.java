package com.examly.springapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name="question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(nullable=false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private QuestionType type;

    @Column(nullable=false)
    private String options; // For MC: comma-separated 4 options, For TF: "true,false"

    @Column(nullable=false)
    private String answer;

    public enum QuestionType {
        MULTIPLE_CHOICE,
        TRUE_FALSE
    }

    // Validation method for options based on type
    public void validate() {
        if (type == QuestionType.MULTIPLE_CHOICE) {
            String[] opts = options.split(",");
            if (opts.length != 4) {
                throw new IllegalArgumentException("Multiple choice questions must have exactly 4 options");
            }
        } else if (type == QuestionType.TRUE_FALSE) {
            if (!options.equals("true,false")) {
                throw new IllegalArgumentException("True/False questions must have options 'true,false'");
            }
            if (!answer.equals("true") && !answer.equals("false")) {
                throw new IllegalArgumentException("True/False questions must have answer 'true' or 'false'");
            }
        }
    }
}