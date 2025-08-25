package com.examly.springapp.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.examly.springapp.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        List<String> errors=ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error->error.getField()+":"+error.getDefaultMessage())
        .collect(Collectors.toList());
        ErrorResponse errorResponse=new ErrorResponse(400,"Validation failed",errors);
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex){
        ErrorResponse errorResponse=new ErrorResponse(404, "Not Found", List.of(ex.getMessage())
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
        
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse>handleResponseStatusException(ResponseStatusException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getStatusCode().value(),ex.getReason(),List.of(ex.getMessage())
        );
        return new ResponseEntity<>(errorResponse,ex.getStatusCode());
    }
}
