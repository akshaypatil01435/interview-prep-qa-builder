package com.interviewprep.backend.repository;

import com.interviewprep.backend.entity.UserQuestionProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserQuestionProgressRepository extends JpaRepository<UserQuestionProgress, Long> {
    Optional<UserQuestionProgress> findByUserIdAndQuestionId(Long userId, Long questionId);
    Set<UserQuestionProgress> findByUserIdAndQuestionIdInAndSolvedTrue(Long userId, Set<Long> questionIds);
    void deleteByQuestionId(Long questionId);
    List<UserQuestionProgress> findByUserId(Long userId);
}
