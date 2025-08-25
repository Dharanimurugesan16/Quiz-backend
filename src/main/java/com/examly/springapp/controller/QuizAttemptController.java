package com.examly.springapp.controller;

import com.examly.springapp.model.Quiz;
import com.examly.springapp.model.QuizAttempt;
import com.examly.springapp.service.QuizAttemptService;
import com.examly.springapp.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz-attempt")
@CrossOrigin(origins = "https://online-quiz-platform-one.vercel.app")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final QuizService quizService;

    public QuizAttemptController(QuizAttemptService quizAttemptService, QuizService quizService) {
        this.quizAttemptService = quizAttemptService;
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveQuizAttempt(@RequestBody QuizAttempt quizAttempt) {
        try {
            QuizAttempt savedAttempt = quizAttemptService.saveQuizAttempt(quizAttempt);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("attemptId", savedAttempt.getAttemptId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserQuizAttempts(@PathVariable String userId) {
        try {
            System.out.println("Fetching attempts for userId: " + userId);
            List<QuizAttempt> attempts = quizAttemptService.getUserQuizAttempts(userId);
            System.out.println("Found attempts: " + attempts.size());
            List<Map<String, Object>> response = attempts.stream().map(attempt -> {
                System.out.println("Processing attemptId: " + attempt.getAttemptId() + ", quizId: " + attempt.getQuizId());
                Map<String, Object> attemptData = new HashMap<>();
                attemptData.put("attemptId", attempt.getAttemptId());
                attemptData.put("quizId", attempt.getQuizId());
                attemptData.put("answers", attempt.getAnswers());
                attemptData.put("score", Math.round((double) attempt.getScore() / attempt.getTotalQuestions() * 100));
                attemptData.put("totalQuestions", attempt.getTotalQuestions());
                attemptData.put("correctAnswers", attempt.getScore());
                attemptData.put("timeSpent", attempt.getTimeSpent());
                attemptData.put("completedDate", attempt.getCompletedDate().toString());
                attemptData.put("attempts", attempt.getAttempts());
                try {
                    Quiz quiz = quizService.getQuizById(attempt.getQuizId(), true); // Bypass deadline
                    System.out.println("Quiz found: " + quiz.getTitle());
                    attemptData.put("quizTitle", quiz.getTitle());
                    attemptData.put("quizDescription", quiz.getDescription());
                    attemptData.put("difficulty", quiz.getDifficulty().toString());
                    attemptData.put("category", quiz.getCategory() != null ? quiz.getCategory() : "Unknown");
                } catch (RuntimeException e) {
                    System.out.println("Quiz not found for quizId: " + attempt.getQuizId() + ", error: " + e.getMessage());
                    attemptData.put("quizTitle", "Quiz Unavailable");
                    attemptData.put("quizDescription", "This quiz is no longer available");
                    attemptData.put("difficulty", "UNKNOWN");
                    attemptData.put("category", "Unknown");
                }
                return attemptData;
            }).collect(Collectors.toList());
            System.out.println("Returning response with " + response.size() + " attempts");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Error fetching attempts for userId: " + userId + ", error: " + e.getMessage());
            return ResponseEntity.status(404).body(List.of(Map.of("error", e.getMessage())));
        }
    }

    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<Map<String, Object>> getQuizAttemptById(@PathVariable Long attemptId) {
        try {
            QuizAttempt attempt = quizAttemptService.getQuizAttemptById(attemptId);
            Quiz quiz = quizService.getQuizById(attempt.getQuizId(), true); // Bypass deadline

            Map<String, Object> response = new HashMap<>();
            response.put("attemptId", attempt.getAttemptId());
            response.put("quizId", attempt.getQuizId());
            response.put("quizTitle", quiz.getTitle());
            response.put("quizDescription", quiz.getDescription());
            response.put("score", attempt.getScore());
            response.put("totalQuestions", attempt.getTotalQuestions());
            response.put("timeSpent", attempt.getTimeSpent());
            response.put("completedDate", attempt.getCompletedDate().toString());
            response.put("difficulty", quiz.getDifficulty().toString());
            response.put("category", quiz.getCategory() != null ? quiz.getCategory() : "Unknown");
            response.put("attempts", attempt.getAttempts());

            Map<String, Map<String, String>> answers = new HashMap<>();
            attempt.getAnswers().forEach((questionId, answerDetails) -> {
                Map<String, String> answerData = new HashMap<>();
                answerData.put("selectedAnswer", answerDetails.getSelectedAnswer());
                answerData.put("correctAnswer", answerDetails.getCorrectAnswer());
                answers.put(questionId, answerData);
            });
            response.put("answers", answers);

            response.put("questions", quiz.getQuestions().stream().map(q -> {
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("id", q.getId());
                questionData.put("questionText", q.getText());
                questionData.put("options", q.getOptions());
                questionData.put("correctAnswer", q.getAnswer());
                return questionData;
            }).collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<Map<String, Object>>> getQuizAttemptsByQuizId(@PathVariable Long quizId) {
        try {
            System.out.println("Fetching attempts for quizId: " + quizId);
            List<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsByQuizId(quizId);
            System.out.println("Found attempts: " + attempts.size());

            List<Map<String, Object>> response = attempts.stream().map(attempt -> {
                System.out.println("Processing attemptId: " + attempt.getAttemptId() + ", quizId: " + attempt.getQuizId());
                Map<String, Object> attemptData = new HashMap<>();
                attemptData.put("attemptId", attempt.getAttemptId());
                attemptData.put("quizId", attempt.getQuizId());
                attemptData.put("username", quizAttemptService.getUsernameByUserId(attempt.getUserId())); // Fetch username
                attemptData.put("answers", attempt.getAnswers());
                attemptData.put("score", attempt.getScore());
                attemptData.put("totalQuestions", attempt.getTotalQuestions());
                attemptData.put("percentage", Math.round((double) attempt.getScore() / attempt.getTotalQuestions() * 100));
                attemptData.put("timeSpent", attempt.getTimeSpent());
                attemptData.put("completedDate", attempt.getCompletedDate().toString());
                attemptData.put("attempts", attempt.getAttempts());
                try {
                    Quiz quiz = quizService.getQuizById(attempt.getQuizId(), true); // Bypass deadline
                    System.out.println("Quiz found: " + quiz.getTitle());
                    attemptData.put("quizTitle", quiz.getTitle());
                    attemptData.put("quizDescription", quiz.getDescription());
                    attemptData.put("difficulty", quiz.getDifficulty().toString());
                    attemptData.put("category", quiz.getCategory() != null ? quiz.getCategory() : "Unknown");
                } catch (RuntimeException e) {
                    System.out.println("Quiz not found for quizId: " + attempt.getQuizId() + ", error: " + e.getMessage());
                    attemptData.put("quizTitle", "Quiz Unavailable");
                    attemptData.put("quizDescription", "This quiz is no longer available");
                    attemptData.put("difficulty", "UNKNOWN");
                    attemptData.put("category", "Unknown");
                }
                return attemptData;
            }).collect(Collectors.toList());
            System.out.println("Returning response with " + response.size() + " attempts");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Error fetching attempts for quizId: " + quizId + ", error: " + e.getMessage());
            return ResponseEntity.status(404).body(List.of(Map.of("error", "No attempts found for quiz ID: " + quizId)));
        }
    }
}
