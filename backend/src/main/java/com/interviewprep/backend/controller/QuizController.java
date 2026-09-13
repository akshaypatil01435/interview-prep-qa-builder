package com.interviewprep.backend.controller;

import com.interviewprep.backend.entity.Quiz;
import com.interviewprep.backend.entity.QuizAttempt;
import com.interviewprep.backend.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllActiveQuizzes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<QuizAttempt> startQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.startQuiz(id));
    }

    @PostMapping("/attempt/{attemptId}/submit")
    public ResponseEntity<QuizAttempt> submitQuiz(
            @PathVariable Long attemptId,
            @RequestBody List<Long> selectedOptionIds) {
        return ResponseEntity.ok(quizService.submitQuiz(attemptId, selectedOptionIds));
    }

    @GetMapping("/attempts")
    public ResponseEntity<List<QuizAttempt>> getMyAttempts() {
        return ResponseEntity.ok(quizService.getCurrentUserAttempts());
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<List<QuizAttempt>> getAttemptsForQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getAttemptsForQuiz(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz,
                                           @RequestParam(required = false) Long topicId) {
        return ResponseEntity.ok(quizService.createQuiz(quiz, topicId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quiz) {
        return ResponseEntity.ok(quizService.updateQuiz(id, quiz));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
