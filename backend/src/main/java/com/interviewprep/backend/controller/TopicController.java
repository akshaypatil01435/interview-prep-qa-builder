package com.interviewprep.backend.controller;

import com.interviewprep.backend.entity.Topic;
import com.interviewprep.backend.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {
    @Autowired private TopicRepository topicRepository;

    @GetMapping
    public List<Topic> getAll() {
        return topicRepository.findAll();
    }

    @PostMapping
    public Topic create(@RequestBody Topic topic) {
        return topicRepository.save(topic);
    }
}
