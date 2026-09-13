package com.interviewprep.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Quiz quiz;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Integer score;

    private Integer totalQuestions;

    private Integer correctAnswers;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    public enum AttemptStatus {
        IN_PROGRESS,
        COMPLETED,
        ABANDONED
    }

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }
}
