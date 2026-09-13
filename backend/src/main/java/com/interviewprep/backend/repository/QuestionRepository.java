package com.interviewprep.backend.repository;

import com.interviewprep.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    long countByTopicId(Long topicId);
    List<Question> findByTopicId(Long topicId);
}
