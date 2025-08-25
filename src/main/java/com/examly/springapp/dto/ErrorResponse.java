package com.examly.springapp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    
    private int status;
    private String message;
    private List<String> errors;
}
