package com.interviewprep.backend.repository;

import com.interviewprep.backend.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Topic> findByNameIgnoreCase(String name);
}
