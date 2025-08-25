package com.examly.springapp.service;

import com.examly.springapp.model.Quiz;
import com.examly.springapp.model.QuizAttempt;
import com.examly.springapp.model.Question;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.QuizAttemptRepository;
import com.examly.springapp.repository.QuizRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizService quizService;
    private final JdbcTemplate jdbcTemplate;

    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository, QuizRepository quizRepository, QuizService quizService, JdbcTemplate jdbcTemplate,UserRepository userRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizRepository = quizRepository;
        this.quizService = quizService;
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    @Transactional
    public QuizAttempt saveQuizAttempt(QuizAttempt quizAttempt) {
        // Validate quiz exists
        Quiz quiz = quizRepository.findById(quizAttempt.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizAttempt.getQuizId()));

        // Validate answers
        for (Question question : quiz.getQuestions()) {
            QuizAttempt.AnswerDetails answerDetails = quizAttempt.getAnswers().get(String.valueOf(question.getId()));
            if (answerDetails == null) {
                throw new RuntimeException("Missing answer for question ID: " + question.getId());
            }
            String selectedAnswer = answerDetails.getSelectedAnswer();
            if (selectedAnswer != null) {
                if (question.getType() == Question.QuestionType.MULTIPLE_CHOICE) {
                    String[] options = question.getOptions().split(",");
                    if (!List.of(options).contains(selectedAnswer)) {
                        throw new RuntimeException("Invalid selected answer '" + selectedAnswer + "' for question ID: " + question.getId() + ". Valid options: " + String.join(", ", options));
                    }
                } else if (question.getType() == Question.QuestionType.TRUE_FALSE) {
                    if (!selectedAnswer.equals("true") && !selectedAnswer.equals("false")) {
                        throw new RuntimeException("Invalid selected answer '" + selectedAnswer + "' for true/false question ID: " + question.getId() + ". Expected 'true' or 'false'.");
                    }
                }
            }
        }

        // Check if this is a retake attempt
        boolean isRetake = false;
        try {
            String checkSql = "SELECT is_retake FROM user_quiz WHERE user_id = ? AND quiz_id = ?";
            isRetake = jdbcTemplate.query(checkSql, new Object[]{quizAttempt.getUserId(), quizAttempt.getQuizId()}, rs -> {
                return rs.next() ? rs.getBoolean("is_retake") : false;
            });
        } catch (Exception e) {
            System.err.println("Error checking is_retake: " + e.getMessage());
            // Assume not a retake if query fails
        }

        // Check if user has previous attempts for this quiz
        List<QuizAttempt> existingAttempts = quizAttemptRepository.findByUserIdAndQuizId(
                quizAttempt.getUserId(), quizAttempt.getQuizId());
        int attemptCount = existingAttempts.size() + 1;

        // Validate attempt count
        if (attemptCount > 2) {
            throw new RuntimeException("Maximum attempts (2) exceeded for quiz ID: " + quizAttempt.getQuizId());
        }

        // Set attempt count and completed date
        quizAttempt.setAttempts(attemptCount);
        quizAttempt.setCompletedDate(LocalDateTime.now());

        // Save the attempt
        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttempt);

        // If this was a retake, remove the user_quiz entry
        if (isRetake) {
            try {
                String deleteSql = "DELETE FROM user_quiz WHERE user_id = ? AND quiz_id = ?";
                jdbcTemplate.update(deleteSql, quizAttempt.getUserId(), quizAttempt.getQuizId());
            } catch (Exception e) {
                System.err.println("Error deleting user_quiz entry: " + e.getMessage());
            }
        }

        return savedAttempt;
    }

    public List<QuizAttempt> getUserQuizAttempts(String userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);
        System.out.println("Found " + attempts.size() + " attempts for userId: " + userId);
        // Filter out attempts where the quiz does not exist
        List<QuizAttempt> filteredAttempts = attempts.stream()
                .filter(attempt -> {
                    boolean exists = quizService.existsById(attempt.getQuizId());
                    System.out.println("Checking quizId: " + attempt.getQuizId() + ", exists: " + exists);
                    return exists;
                })
                .collect(Collectors.toList());
        System.out.println("Returning " + filteredAttempts.size() + " filtered attempts");
        return filteredAttempts;
    }

    public QuizAttempt getQuizAttemptById(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found with ID: " + attemptId));
        // Verify quiz exists
        if (!quizService.existsById(attempt.getQuizId())) {
            throw new RuntimeException("Quiz no longer exists for attempt ID: " + attemptId);
        }
        return attempt;
    }
    public List<QuizAttempt> getQuizAttemptsByQuizId(Long quizId) {
        return quizAttemptRepository.findByQuizId(quizId);
    }

    public String getUsernameByUserId(String userId) {
        try {
            Long id = Long.parseLong(userId); // Convert String to Long
            return userRepository.findById(id)
                    .map(User::getUsername)
                    .orElse("Unknown User");
        } catch (NumberFormatException e) {
            return "Unknown User"; // Handle invalid String-to-Long conversion
        }
    }
}
