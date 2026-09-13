package com.interviewprep.backend.controller;

import com.interviewprep.backend.entity.PracticeRecord;
import com.interviewprep.backend.service.PracticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class PracticeController {

    private final PracticeService practiceService;

    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<PracticeRecord>> getHistory() {
        return ResponseEntity.ok(practiceService.getCurrentUserHistory());
    }

    @PostMapping("/record")
    public ResponseEntity<PracticeRecord> recordPractice(
            @RequestParam Long questionId,
            @RequestParam PracticeRecord.PracticeType type,
            @RequestParam(required = false) PracticeRecord.PracticeResult result,
            @RequestParam(required = false) Integer score) {
        return ResponseEntity.ok(practiceService.recordPractice(questionId, type, result, score));
    }
}
