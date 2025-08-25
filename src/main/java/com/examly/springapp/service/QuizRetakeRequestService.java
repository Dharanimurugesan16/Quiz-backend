package com.examly.springapp.service;

import com.examly.springapp.model.Quiz;
import com.examly.springapp.model.QuizRetakeRequest;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.QuizRetakeRequestRepository;
import com.examly.springapp.repository.QuizRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizRetakeRequestService {

    private final QuizRetakeRequestRepository retakeRequestRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public QuizRetakeRequestService(
            QuizRetakeRequestRepository retakeRequestRepository,
            QuizRepository quizRepository,
            UserRepository userRepository,
            JdbcTemplate jdbcTemplate) {
        this.retakeRequestRepository = retakeRequestRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public QuizRetakeRequest createRetakeRequest(Long userId, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        QuizRetakeRequest request = QuizRetakeRequest.builder()
                .userId(userId)
                .quizId(quizId)
                .studentUsername(user.getUsername())
                .quizTitle(quiz.getTitle())
                .status(QuizRetakeRequest.Status.PENDING)
                .build();

        return retakeRequestRepository.save(request);
    }

    public List<QuizRetakeRequest> getAllRetakeRequests() {
        return retakeRequestRepository.findAll();
    }

    @Transactional
    public void approveRetakeRequest(Long requestId) {
        QuizRetakeRequest request = retakeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Retake request not found with ID: " + requestId));
        if (request.getStatus() != QuizRetakeRequest.Status.PENDING) {
            throw new RuntimeException("Retake request is not in PENDING status");
        }
        request.setStatus(QuizRetakeRequest.Status.APPROVED);
        retakeRequestRepository.save(request);

        String sql = "INSERT INTO user_quiz (user_id, quiz_id, is_retake) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, request.getUserId(), request.getQuizId(), true);
    }

    @Transactional
    public void rejectRetakeRequest(Long requestId) {
        QuizRetakeRequest request = retakeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Retake request not found with ID: " + requestId));
        if (request.getStatus() != QuizRetakeRequest.Status.PENDING) {
            throw new RuntimeException("Retake request is not in PENDING status");
        }
        request.setStatus(QuizRetakeRequest.Status.REJECTED);
        retakeRequestRepository.save(request);
    }

    public boolean hasPendingRequest(Long userId, Long quizId) {
        return retakeRequestRepository.existsByUserIdAndQuizIdAndStatus(
                userId, quizId, QuizRetakeRequest.Status.PENDING
        );
    }

    public List<QuizRetakeRequest> getRetakeRequestsByQuizId(Long quizId) {
        return retakeRequestRepository.findByQuizId(quizId);
    }

    public void deleteRetakeRequest(Long requestId) {
        if (!retakeRequestRepository.existsById(requestId)) {
            throw new RuntimeException("Retake request not found with ID: " + requestId);
        }
        retakeRequestRepository.deleteById(requestId);
    }
}