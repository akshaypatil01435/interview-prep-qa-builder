package com.interviewprep.backend.controller;

import com.interviewprep.backend.entity.Question;
import com.interviewprep.backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired private QuestionRepository questionRepository;

    @GetMapping
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @GetMapping("/topic/{topicId}")
    public List<Question> getByTopic(@PathVariable Long topicId) {
        return questionRepository.findByTopicId(topicId);
    }

    @PostMapping
    public Question create(@RequestBody Question question) {
        return questionRepository.save(question);
    }
}