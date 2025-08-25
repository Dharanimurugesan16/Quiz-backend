package com.examly.springapp.controller;

import com.examly.springapp.model.User;
import com.examly.springapp.model.Quiz;
import com.examly.springapp.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/students")
    public List<User> getAllStudents() {
        return userService.getAllStudents();
    }

    @PostMapping("/assign")
    public String assignQuiz(@RequestParam Long userId, @RequestParam Long quizId) {
        userService.assignQuiz(userId, quizId);
        return "Quiz assigned successfully";
    }

    @GetMapping("/{id}/quizzes")
    public List<Quiz> getAssignedQuizzes(@PathVariable Long id) {
        return userService.getAssignedQuizzes(id);
    }
}
