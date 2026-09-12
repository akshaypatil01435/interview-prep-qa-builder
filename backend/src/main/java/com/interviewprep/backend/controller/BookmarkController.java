package com.interviewprep.backend.controller;

import com.interviewprep.backend.entity.Bookmark;
import com.interviewprep.backend.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    @Autowired private BookmarkRepository bookmarkRepository;

    @PostMapping
    public Bookmark add(@RequestBody Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }

    @GetMapping("/user/{userId}")
    public List<Bookmark> getByUser(@PathVariable Long userId) {
        return bookmarkRepository.findByUserId(userId);
    }
}