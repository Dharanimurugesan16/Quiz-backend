package com.examly.springapp.service;

import com.examly.springapp.model.User;
import com.examly.springapp.model.Quiz;
import com.examly.springapp.repository.UserRepository;
import com.examly.springapp.repository.QuizRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public UserService(UserRepository userRepository, QuizRepository quizRepository) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
    }

    // fetch only students
    public List<User> getAllStudents() {
        return userRepository.findAll()
                .stream()
                .filter(u -> "ROLE_STUDENT".equals(u.getRole()))
                .toList();
    }
    public void assignQuiz(Long userId, Long quizId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Add quiz to user's assigned quizzes if not already present
        if (!user.getAssignedQuizzes().contains(quiz)) {
            user.getAssignedQuizzes().add(quiz);
            userRepository.save(user);  // only save user; cascade handles join table
        }
    }

    // get quizzes assigned to a student
    public List<Quiz> getAssignedQuizzes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getAssignedQuizzes();
    }
}
