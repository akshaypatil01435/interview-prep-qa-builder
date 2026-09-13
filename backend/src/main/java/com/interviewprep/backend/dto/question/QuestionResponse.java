package com.interviewprep.backend.dto.question;

import com.interviewprep.backend.dto.topic.TopicResponse;
import com.interviewprep.backend.entity.Question;
import java.time.Instant;

public record QuestionResponse(Long id, String questionText, String answerText, String difficulty, TopicResponse topic, boolean bookmarked, boolean solved, Instant createdAt, Instant updatedAt) {
    public static QuestionResponse from(Question question, boolean bookmarked, boolean solved) {
        return new QuestionResponse(question.getId(), question.getQuestionText(), question.getAnswerText(), question.getDifficulty().name(), TopicResponse.from(question.getTopic()), bookmarked, solved, question.getCreatedAt(), question.getUpdatedAt());
    }
}
