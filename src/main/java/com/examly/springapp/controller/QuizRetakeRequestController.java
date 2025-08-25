package com.examly.springapp.controller;

import com.examly.springapp.model.QuizRetakeRequest;
import com.examly.springapp.service.QuizRetakeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz-retake")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizRetakeRequestController {

    private final QuizRetakeRequestService retakeRequestService;

    public QuizRetakeRequestController(QuizRetakeRequestService retakeRequestService) {
        this.retakeRequestService = retakeRequestService;
    }

    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> createRetakeRequest(
            @RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            Long quizId = Long.valueOf(requestBody.get("quizId").toString());
            // Check for existing pending request
            if (retakeRequestService.hasPendingRequest(userId, quizId)) {
                return ResponseEntity.status(400).body(Map.of(
                        "success", false,
                        "error", "A pending retake request already exists for this quiz"
                ));
            }
            QuizRetakeRequest request = retakeRequestService.createRetakeRequest(userId, quizId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "requestId", request.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/requests")
    public List<QuizRetakeRequest> getRetakeRequests(@RequestParam(required = false) Long quizId) {
        if (quizId != null) {
            return retakeRequestService.getRetakeRequestsByQuizId(quizId);
        }
        return retakeRequestService.getAllRetakeRequests();
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<Map<String, Object>> approveRetakeRequest(@PathVariable Long requestId) {
        try {
            retakeRequestService.approveRetakeRequest(requestId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<Map<String, Object>> rejectRetakeRequest(@PathVariable Long requestId) {
        try {
            retakeRequestService.rejectRetakeRequest(requestId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity<Map<String, Object>> deleteRetakeRequest(@PathVariable Long requestId) {
        try {
            retakeRequestService.deleteRetakeRequest(requestId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}