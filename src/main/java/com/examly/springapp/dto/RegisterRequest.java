package com.examly.springapp.dto;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // quiztaker or quizmaker

    // getters & setters
}
