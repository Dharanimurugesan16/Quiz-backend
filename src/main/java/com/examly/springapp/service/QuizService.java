package com.examly.springapp.service;

import com.examly.springapp.model.Quiz;
import com.examly.springapp.repository.QuizRepository;
import com.examly.springapp.repository.QuizRetakeRequestRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizRetakeRequestRepository retakeRequestRepository;
    private final JdbcTemplate jdbcTemplate;

    public QuizService(QuizRepository quizRepository, QuizRetakeRequestRepository retakeRequestRepository, JdbcTemplate jdbcTemplate) {
        this.quizRepository = quizRepository;
        this.retakeRequestRepository = retakeRequestRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Quiz createQuiz(Quiz quiz) {
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());
        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setTitle(quizDetails.getTitle()); // Fixed: Removed incorrect 't' reference
        quiz.setDescription(quizDetails.getDescription());
        quiz.setTimeLimit(quizDetails.getTimeLimit());
        quiz.setQuestions(quizDetails.getQuestions());
        quiz.setDifficulty(quizDetails.getDifficulty());
        quiz.setDeadline(quizDetails.getDeadline());
        quiz.setUpdatedAt(LocalDateTime.now());
        return quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Quiz not found with ID: " + id);
        }
        retakeRequestRepository.deleteByQuizId(id);
        deleteUserQuizAssignments(id);
        quizRepository.deleteById(id);
    }

    public void deleteUserQuizAssignments(Long quizId) {
        String sql = "DELETE FROM user_quiz WHERE quiz_id = ?";
        jdbcTemplate.update(sql, quizId);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Long quizId, boolean bypassDeadline) {
        System.out.println("Fetching quiz with ID: " + quizId + ", bypassDeadline: " + bypassDeadline);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));
        if (!bypassDeadline && quiz.getDeadline() != null && quiz.getDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Quiz is no longer available due to passed deadline");
        }
        System.out.println("Quiz found: " + quiz.getTitle());
        return quiz;
    }

    public Quiz getQuizById(Long quizId) {
        return getQuizById(quizId, false); // Default: enforce deadline
    }

    public boolean existsById(Long id) {
        return quizRepository.existsById(id);
    }
}