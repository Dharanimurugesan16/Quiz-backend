package com.examly.springapp.repository;

import com.examly.springapp.model.QuizRetakeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRetakeRequestRepository extends JpaRepository<QuizRetakeRequest, Long> {
    boolean existsByUserIdAndQuizIdAndStatus(Long userId, Long quizId, QuizRetakeRequest.Status status);
    List<QuizRetakeRequest> findByQuizId(Long quizId);

    @Modifying
    @Query("DELETE FROM QuizRetakeRequest r WHERE r.quizId = :quizId")
    void deleteByQuizId(@Param("quizId") Long quizId);
}