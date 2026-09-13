package com.interviewprep.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class PracticeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @Enumerated(EnumType.STRING)
    private PracticeType practiceType;

    @Enumerated(EnumType.STRING)
    private PracticeResult result;

    private Integer score;

    private LocalDateTime practicedAt;

    public enum PracticeType {
        QUESTION,
        QUIZ,
        MOCK_INTERVIEW
    }

    public enum PracticeResult {
        SOLVED,
        UNSOLVED,
        NEEDS_REVISION,
        COMPLETED
    }

    @PrePersist
    protected void onCreate() {
        practicedAt = LocalDateTime.now();
    }
}
