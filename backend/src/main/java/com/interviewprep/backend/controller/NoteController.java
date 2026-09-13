package com.interviewprep.backend.controller;

import com.interviewprep.backend.entity.Note;
import com.interviewprep.backend.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping("/user/{userId}")
    public List<Note> getByUser(@PathVariable Long userId) {
        return noteRepository.findByUserId(userId);
    }

    @PostMapping
    public Note create(@RequestBody Note note) {
        return noteRepository.save(note);
    }
}
