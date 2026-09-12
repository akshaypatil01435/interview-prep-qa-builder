package com.interviewprep.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    private String difficulty;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;
}
