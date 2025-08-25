package com.examly.springapp.service;

import com.examly.springapp.dto.LoginRequest;
import com.examly.springapp.dto.RegisterRequest;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already exists!";
        }
        String role = "ROLE_" + request.getRole().toUpperCase(); // ROLE_QUIZTAKER or ROLE_QUIZMAKER
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                role
        );
        userRepository.save(user);
        return "User registered successfully!";
    }

    public String login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> "Login successful! Role: " + user.getRole())
                .orElse("Invalid credentials!");
    }
    public User authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }

}

