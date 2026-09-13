package com.interviewprep.backend.controller;

import com.interviewprep.backend.entity.Note;
import com.interviewprep.backend.entity.Question;
import com.interviewprep.backend.entity.User;
import com.interviewprep.backend.repository.NoteRepository;
import com.interviewprep.backend.repository.QuestionRepository;
import com.interviewprep.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class NoteController {

    private final NoteRepository noteRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public NoteController(NoteRepository noteRepository, QuestionRepository questionRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getCurrentUserNotes() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(noteRepository.findByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNote(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(note);
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody NoteRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Question question = questionRepository.findById(request.questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Note note = new Note();
        note.setUser(user);
        note.setQuestion(question);
        note.setContent(request.content);

        return ResponseEntity.ok(noteRepository.save(note));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody NoteRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        note.setContent(request.content);
        return ResponseEntity.ok(noteRepository.save(note));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        noteRepository.delete(note);
        return ResponseEntity.noContent().build();
    }

    public static class NoteRequest {
        private Long questionId;
        private String content;

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
