package com.interviewprep.backend.repository;

import com.interviewprep.backend.entity.PracticeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeRecordRepository extends JpaRepository<PracticeRecord, Long> {
    List<PracticeRecord> findByUserIdOrderByPracticedAtDesc(Long userId);
}
