package com.interviewprep.backend.controller;

import com.interviewprep.backend.dto.common.PageResponse;
import com.interviewprep.backend.dto.question.QuestionResponse;
import com.interviewprep.backend.entity.Difficulty;
import com.interviewprep.backend.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    public QuestionController(QuestionService questionService) { this.questionService = questionService; }

    @GetMapping
    public PageResponse<QuestionResponse> getAll(Authentication authentication, @RequestParam(required = false) String query, @RequestParam(required = false) Long topicId, @RequestParam(required = false) Difficulty difficulty, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size, @RequestParam(defaultValue = "createdAt") String sort) {
        return questionService.search(authentication.getName(), query, topicId, difficulty, page, size, sort);
    }

    @GetMapping("/{id}")
    public QuestionResponse getById(@PathVariable Long id, Authentication authentication) {
        return questionService.get(id, authentication.getName());
    }

    @PutMapping("/{id}/solved")
    public QuestionResponse markSolved(@PathVariable Long id, Authentication authentication) {
        return questionService.setSolved(id, authentication.getName(), true);
    }

    @DeleteMapping("/{id}/solved")
    public QuestionResponse markUnsolved(@PathVariable Long id, Authentication authentication) {
        return questionService.setSolved(id, authentication.getName(), false);
    }

    @PutMapping("/{id}/bookmark")
    public QuestionResponse bookmark(@PathVariable Long id, Authentication authentication) { return questionService.setBookmark(id, authentication.getName(), true); }

    @DeleteMapping("/{id}/bookmark")
    public QuestionResponse unbookmark(@PathVariable Long id, Authentication authentication) { return questionService.setBookmark(id, authentication.getName(), false); }
}
