package com.interviewprep.backend.controller;

import com.interviewprep.backend.dto.question.QuestionRequest;
import com.interviewprep.backend.dto.question.QuestionResponse;
import com.interviewprep.backend.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {

    private final QuestionService questionService;
    public AdminQuestionController(QuestionService questionService) { this.questionService = questionService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponse create(@Valid @RequestBody QuestionRequest request) { return questionService.create(request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        questionService.delete(id);
    }

    @PutMapping("/{id}")
    public QuestionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest updated) {
        return questionService.update(id, updated);
    }
}
