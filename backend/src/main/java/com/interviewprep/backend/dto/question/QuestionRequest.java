package com.interviewprep.backend.dto.question;

import com.interviewprep.backend.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionRequest(
        @NotBlank @Size(max = 10000) String questionText,
        @NotBlank @Size(max = 30000) String answerText,
        @NotNull Difficulty difficulty,
        @NotNull Long topicId) {}
