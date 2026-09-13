package com.interviewprep.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    // MCQ support
    @Enumerated(EnumType.STRING)
    private QuestionType questionType = QuestionType.TEXT;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @Enumerated(EnumType.STRING)
    private McqOption correctOption;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum QuestionType {
        TEXT,
        MCQ
    }

    public enum McqOption {
        A, B, C, D
    }
}
