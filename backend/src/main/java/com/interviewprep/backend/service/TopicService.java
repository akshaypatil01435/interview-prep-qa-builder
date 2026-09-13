package com.interviewprep.backend.service;

import com.interviewprep.backend.dto.topic.TopicRequest;
import com.interviewprep.backend.dto.topic.TopicResponse;
import com.interviewprep.backend.entity.Topic;
import com.interviewprep.backend.exception.ConflictException;
import com.interviewprep.backend.exception.ResourceNotFoundException;
import com.interviewprep.backend.repository.QuestionRepository;
import com.interviewprep.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;

    public TopicService(TopicRepository topicRepository, QuestionRepository questionRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> getAll() {
        return topicRepository.findAll().stream()
                .sorted(Comparator.comparing(Topic::getName, String.CASE_INSENSITIVE_ORDER))
                .map(TopicResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TopicResponse getById(Long id) {
        return TopicResponse.from(getEntity(id));
    }

    @Transactional
    public TopicResponse create(TopicRequest request) {
        String trimmedName = request.name().trim();
        if (topicRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ConflictException("A topic with this name already exists");
        }

        Topic topic = new Topic();
        topic.setName(trimmedName);
        topic.setDescription(request.description() == null ? null : request.description().trim());

        return TopicResponse.from(topicRepository.save(topic));
    }

    @Transactional
    public TopicResponse update(Long id, TopicRequest request) {
        Topic topic = getEntity(id);
        String trimmedName = request.name().trim();

        topicRepository.findByNameIgnoreCase(trimmedName).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException("A topic with this name already exists");
            }
        });

        topic.setName(trimmedName);
        topic.setDescription(request.description() == null ? null : request.description().trim());

        return TopicResponse.from(topicRepository.save(topic));
    }

    @Transactional
    public void delete(Long id) {
        Topic topic = getEntity(id);
        long questionCount = questionRepository.countByTopicId(id);
        if (questionCount > 0) {
            throw new ConflictException("Cannot delete topic: it contains " + questionCount + " question(s). Please delete or reassign them first.");
        }
        topicRepository.delete(topic);
    }

    public Topic getEntity(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
    }
}
