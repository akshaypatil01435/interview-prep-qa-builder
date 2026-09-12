package com.interviewprep.backend.controller;

import com.interviewprep.backend.entity.Question;
import com.interviewprep.backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return "Deleted";
    }

    @PutMapping("/{id}")
    public Question update(
            @PathVariable Long id,
            @RequestBody Question updated) {

        updated.setId(id);
        return questionRepository.save(updated);
    }
}