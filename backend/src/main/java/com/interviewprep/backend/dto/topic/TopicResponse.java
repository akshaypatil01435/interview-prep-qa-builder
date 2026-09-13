package com.interviewprep.backend.dto.topic;

import com.interviewprep.backend.entity.Topic;

public record TopicResponse(Long id, String name, String description) {
    public static TopicResponse from(Topic topic) { return new TopicResponse(topic.getId(), topic.getName(), topic.getDescription()); }
}
