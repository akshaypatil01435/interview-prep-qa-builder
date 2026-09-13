package com.interviewprep.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @ManyToMany
    private List<Question> questions = new ArrayList<>();

    private Integer timeLimitMinutes;

    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
