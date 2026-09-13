package com.interviewprep.backend.repository;

import com.interviewprep.backend.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserIdOrderByCompletedAtDesc(Long userId);
    List<QuizAttempt> findByQuizIdAndUserId(Long quizId, Long userId);
}
