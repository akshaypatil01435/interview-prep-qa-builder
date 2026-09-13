package com.interviewprep.backend.controller;

import com.interviewprep.backend.dto.question.QuestionResponse;
import com.interviewprep.backend.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final QuestionService questionService;
    public BookmarkController(QuestionService questionService) { this.questionService = questionService; }

    @GetMapping
    public List<QuestionResponse> getMine(Authentication authentication) {
        return questionService.bookmarks(authentication.getName());
    }

    @PutMapping("/{questionId}")
    public QuestionResponse add(@PathVariable Long questionId, Authentication authentication) {
        return questionService.setBookmark(questionId, authentication.getName(), true);
    }

    @DeleteMapping("/{questionId}")
    public QuestionResponse remove(@PathVariable Long questionId, Authentication authentication) {
        return questionService.setBookmark(questionId, authentication.getName(), false);
    }
}
