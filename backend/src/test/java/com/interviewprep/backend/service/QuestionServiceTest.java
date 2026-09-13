package com.interviewprep.backend.service;

import com.interviewprep.backend.dto.question.QuestionRequest;
import com.interviewprep.backend.dto.question.QuestionResponse;
import com.interviewprep.backend.entity.*;
import com.interviewprep.backend.exception.ResourceNotFoundException;
import com.interviewprep.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questions;

    @Mock
    private TopicRepository topics;

    @Mock
    private UserRepository users;

    @Mock
    private BookmarkRepository bookmarks;

    @Mock
    private UserQuestionProgressRepository progress;

    @Mock
    private NoteRepository notes;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void getQuestionReturnsResponseWithSolvedAndBookmarkFlags() {
        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Java");

        Question question = new Question();
        question.setId(10L);
        question.setQuestionText("What is JVM?");
        question.setAnswerText("Java Virtual Machine");
        question.setDifficulty(Difficulty.EASY);
        question.setTopic(topic);

        User user = new User();
        user.setId(2L);
        user.setEmail("user@example.com");

        when(questions.findById(10L)).thenReturn(Optional.of(question));
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bookmarks.findByUserIdAndQuestionId(2L, 10L)).thenReturn(Optional.of(new Bookmark()));

        UserQuestionProgress uqp = new UserQuestionProgress();
        uqp.setSolved(true);
        when(progress.findByUserIdAndQuestionId(2L, 10L)).thenReturn(Optional.of(uqp));

        QuestionResponse response = questionService.get(10L, "user@example.com");

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("What is JVM?", response.questionText());
        assertTrue(response.bookmarked());
        assertTrue(response.solved());
    }

    @Test
    void createQuestionSucceeds() {
        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Java");

        when(topics.findById(1L)).thenReturn(Optional.of(topic));
        when(questions.save(any(Question.class))).thenAnswer(inv -> {
            Question q = inv.getArgument(0);
            q.setId(100L);
            return q;
        });

        QuestionRequest req = new QuestionRequest("What is immutability?", "Cannot be modified", Difficulty.MEDIUM, 1L);
        QuestionResponse res = questionService.create(req);

        assertNotNull(res);
        assertEquals(100L, res.id());
        assertEquals("What is immutability?", res.questionText());
        assertEquals("MEDIUM", res.difficulty());
    }

    @Test
    void updateQuestionSucceeds() {
        Topic topic = new Topic();
        topic.setId(2L);
        topic.setName("Spring");

        Question existing = new Question();
        existing.setId(50L);
        existing.setQuestionText("Old question");
        existing.setAnswerText("Old answer");
        existing.setDifficulty(Difficulty.EASY);
        existing.setTopic(topic);

        when(questions.findById(50L)).thenReturn(Optional.of(existing));
        when(topics.findById(2L)).thenReturn(Optional.of(topic));
        when(questions.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        QuestionRequest req = new QuestionRequest("Updated question", "Updated answer", Difficulty.HARD, 2L);
        QuestionResponse res = questionService.update(50L, req);

        assertEquals("Updated question", res.questionText());
        assertEquals("Updated answer", res.answerText());
        assertEquals("HARD", res.difficulty());
    }

    @Test
    void deleteQuestionCascadesBookmarksProgressAndNotes() {
        Question existing = new Question();
        existing.setId(77L);

        when(questions.findById(77L)).thenReturn(Optional.of(existing));

        questionService.delete(77L);

        verify(bookmarks).deleteByQuestionId(77L);
        verify(progress).deleteByQuestionId(77L);
        verify(notes).deleteByQuestionId(77L);
        verify(questions).delete(existing);
    }

    @Test
    void setSolvedMarksQuestionAsSolved() {
        User user = new User();
        user.setId(5L);
        user.setEmail("test@example.com");

        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Java");

        Question question = new Question();
        question.setId(30L);
        question.setTopic(topic);
        question.setDifficulty(Difficulty.EASY);

        when(users.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(questions.findById(30L)).thenReturn(Optional.of(question));
        when(progress.findByUserIdAndQuestionId(5L, 30L)).thenReturn(Optional.empty());

        questionService.setSolved(30L, "test@example.com", true);

        verify(progress).save(argThat(UserQuestionProgress::isSolved));
    }

    @Test
    void setBookmarkAddsBookmarkWhenTrue() {
        User user = new User();
        user.setId(5L);
        user.setEmail("test@example.com");

        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Java");

        Question question = new Question();
        question.setId(30L);
        question.setTopic(topic);
        question.setDifficulty(Difficulty.EASY);

        when(users.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(questions.findById(30L)).thenReturn(Optional.of(question));
        when(bookmarks.findByUserIdAndQuestionId(5L, 30L)).thenReturn(Optional.empty());

        questionService.setBookmark(30L, "test@example.com", true);

        verify(bookmarks).save(any(Bookmark.class));
    }

    @Test
    void setBookmarkRemovesBookmarkWhenFalse() {
        User user = new User();
        user.setId(5L);
        user.setEmail("test@example.com");

        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Java");

        Question question = new Question();
        question.setId(30L);
        question.setTopic(topic);
        question.setDifficulty(Difficulty.EASY);

        Bookmark existing = new Bookmark();
        existing.setId(99L);

        when(users.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(questions.findById(30L)).thenReturn(Optional.of(question));
        when(bookmarks.findByUserIdAndQuestionId(5L, 30L)).thenReturn(Optional.of(existing));

        questionService.setBookmark(30L, "test@example.com", false);

        verify(bookmarks).delete(existing);
    }

    @Test
    void bookmarksReturnsListUsingBatchProgressQuery() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Java");

        Question q1 = new Question();
        q1.setId(10L);
        q1.setQuestionText("Q1");
        q1.setDifficulty(Difficulty.EASY);
        q1.setTopic(topic);

        Question q2 = new Question();
        q2.setId(20L);
        q2.setQuestionText("Q2");
        q2.setDifficulty(Difficulty.MEDIUM);
        q2.setTopic(topic);

        Bookmark b1 = new Bookmark();
        b1.setId(101L);
        b1.setQuestion(q1);

        Bookmark b2 = new Bookmark();
        b2.setId(102L);
        b2.setQuestion(q2);

        UserQuestionProgress p1 = new UserQuestionProgress();
        p1.setQuestion(q1);
        p1.setSolved(true);

        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bookmarks.findByUserId(1L)).thenReturn(List.of(b1, b2));
        when(progress.findByUserIdAndQuestionIdInAndSolvedTrue(1L, Set.of(10L, 20L)))
                .thenReturn(Set.of(p1));

        List<QuestionResponse> result = questionService.bookmarks("user@example.com");

        assertEquals(2, result.size());
        assertTrue(result.get(0).bookmarked());
        assertTrue(result.get(0).solved()); // q1 is solved
        assertTrue(result.get(1).bookmarked());
        assertFalse(result.get(1).solved()); // q2 is not solved
    }
}
