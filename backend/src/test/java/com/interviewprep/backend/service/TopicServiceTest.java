package com.interviewprep.backend.service;

import com.interviewprep.backend.dto.topic.TopicRequest;
import com.interviewprep.backend.dto.topic.TopicResponse;
import com.interviewprep.backend.entity.Topic;
import com.interviewprep.backend.exception.ConflictException;
import com.interviewprep.backend.exception.ResourceNotFoundException;
import com.interviewprep.backend.repository.QuestionRepository;
import com.interviewprep.backend.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    void getAllReturnsSortedTopics() {
        Topic t1 = new Topic();
        t1.setId(1L);
        t1.setName("SQL");
        Topic t2 = new Topic();
        t2.setId(2L);
        t2.setName("Java");

        when(topicRepository.findAll()).thenReturn(List.of(t1, t2));

        List<TopicResponse> result = topicService.getAll();

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).name());
        assertEquals("SQL", result.get(1).name());
    }

    @Test
    void createTopicSucceeds() {
        when(topicRepository.existsByNameIgnoreCase("Spring Boot")).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenAnswer(inv -> {
            Topic t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TopicResponse res = topicService.create(new TopicRequest("  Spring Boot  ", "  Framework basics  "));

        assertEquals(10L, res.id());
        assertEquals("Spring Boot", res.name());
        assertEquals("Framework basics", res.description());
    }

    @Test
    void createTopicRejectsDuplicateName() {
        when(topicRepository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                topicService.create(new TopicRequest("Java", "Java desc")));
    }

    @Test
    void updateTopicSucceeds() {
        Topic existing = new Topic();
        existing.setId(5L);
        existing.setName("Java 8");
        when(topicRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(topicRepository.findByNameIgnoreCase("Java 17")).thenReturn(Optional.empty());
        when(topicRepository.save(any(Topic.class))).thenAnswer(inv -> inv.getArgument(0));

        TopicResponse res = topicService.update(5L, new TopicRequest("Java 17", "Modern Java"));

        assertEquals("Java 17", res.name());
        assertEquals("Modern Java", res.description());
    }

    @Test
    void updateTopicRejectsDuplicateIfDifferentId() {
        Topic existing = new Topic();
        existing.setId(5L);
        existing.setName("Java 8");

        Topic conflict = new Topic();
        conflict.setId(8L);
        conflict.setName("Java 17");

        when(topicRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(topicRepository.findByNameIgnoreCase("Java 17")).thenReturn(Optional.of(conflict));

        assertThrows(ConflictException.class, () ->
                topicService.update(5L, new TopicRequest("Java 17", "Modern Java")));
    }

    @Test
    void deleteTopicSucceedsWhenNoQuestionsAttached() {
        Topic existing = new Topic();
        existing.setId(3L);
        when(topicRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(questionRepository.countByTopicId(3L)).thenReturn(0L);

        topicService.delete(3L);

        verify(topicRepository).delete(existing);
    }

    @Test
    void deleteTopicThrowsConflictWhenQuestionsAttached() {
        Topic existing = new Topic();
        existing.setId(3L);
        when(topicRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(questionRepository.countByTopicId(3L)).thenReturn(5L);

        ConflictException ex = assertThrows(ConflictException.class, () -> topicService.delete(3L));
        assertTrue(ex.getMessage().contains("contains 5 question(s)"));
        verify(topicRepository, never()).delete(any());
    }
}
