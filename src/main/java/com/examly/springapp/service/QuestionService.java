package com.examly.springapp.service;

import com.examly.springapp.model.Question;
import com.examly.springapp.repository.QuestionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final JdbcTemplate jdbcTemplate;

    public QuestionService(QuestionRepository questionRepository, JdbcTemplate jdbcTemplate) {
        this.questionRepository = questionRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Question createQuestion(Question question) {
        question.validate(); // Validate question before saving
        return questionRepository.save(question);
    }

    public Question updateQuestion(Long id, Question questionDetails) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setText(questionDetails.getText());
        question.setType(questionDetails.getType());
        question.setOptions(questionDetails.getOptions());
        question.setAnswer(questionDetails.getAnswer());
        question.validate(); // Validate question before saving
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        // Delete from join table first
        jdbcTemplate.update("DELETE FROM quiz_questions WHERE question_id = ?", id);

        // Then delete the question
        questionRepository.deleteById(id);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }
}