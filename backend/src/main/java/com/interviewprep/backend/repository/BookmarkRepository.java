package com.interviewprep.backend.repository;

import com.interviewprep.backend.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId);
    Set<Bookmark> findByUserIdAndQuestionIdIn(Long userId, Set<Long> questionIds);
    void deleteByQuestionId(Long questionId);
}
