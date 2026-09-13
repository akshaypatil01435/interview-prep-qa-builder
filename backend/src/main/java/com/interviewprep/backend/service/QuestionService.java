package com.interviewprep.backend.service;

import com.interviewprep.backend.dto.common.PageResponse;
import com.interviewprep.backend.dto.question.QuestionRequest;
import com.interviewprep.backend.dto.question.QuestionResponse;
import com.interviewprep.backend.entity.*;
import com.interviewprep.backend.exception.ResourceNotFoundException;
import com.interviewprep.backend.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questions;
    private final TopicRepository topics;
    private final UserRepository users;
    private final BookmarkRepository bookmarks;
    private final UserQuestionProgressRepository progress;
    private final NoteRepository notes;

    public QuestionService(
            QuestionRepository questions,
            TopicRepository topics,
            UserRepository users,
            BookmarkRepository bookmarks,
            UserQuestionProgressRepository progress,
            NoteRepository notes) {
        this.questions = questions;
        this.topics = topics;
        this.users = users;
        this.bookmarks = bookmarks;
        this.progress = progress;
        this.notes = notes;
    }

    @Transactional(readOnly = true)
    public PageResponse<QuestionResponse> search(
            String email,
            String query,
            Long topicId,
            Difficulty difficulty,
            int page,
            int size,
            String sort) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100");
        }

        String sortField = Set.of("createdAt", "updatedAt", "questionText", "difficulty").contains(sort)
                ? sort
                : "createdAt";

        Specification<Question> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (topicId != null) {
                predicates.add(cb.equal(root.get("topic").get("id"), topicId));
            }
            if (difficulty != null) {
                predicates.add(cb.equal(root.get("difficulty"), difficulty));
            }
            if (query != null && !query.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("questionText")), "%" + query.trim().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Question> result = questions.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField)));
        return mapPage(result, email);
    }

    @Transactional(readOnly = true)
    public QuestionResponse get(Long id, String email) {
        return map(getEntity(id), email);
    }

    @Transactional
    public QuestionResponse create(QuestionRequest request) {
        return save(new Question(), request);
    }

    @Transactional
    public QuestionResponse update(Long id, QuestionRequest request) {
        return save(getEntity(id), request);
    }

    @Transactional
    public void delete(Long id) {
        Question question = getEntity(id);
        bookmarks.deleteByQuestionId(id);
        progress.deleteByQuestionId(id);
        notes.deleteByQuestionId(id);
        questions.delete(question);
    }

    @Transactional
    public QuestionResponse setSolved(Long id, String email, boolean solved) {
        User user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Question question = getEntity(id);
        UserQuestionProgress item = progress.findByUserIdAndQuestionId(user.getId(), id)
                .orElseGet(UserQuestionProgress::new);
        item.setUser(user);
        item.setQuestion(question);
        item.setSolved(solved);
        item.setSolvedAt(solved ? Instant.now() : null);
        progress.save(item);
        return map(question, email);
    }

    @Transactional
    public QuestionResponse setBookmark(Long id, String email, boolean bookmarked) {
        User user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Question question = getEntity(id);
        bookmarks.findByUserIdAndQuestionId(user.getId(), id).ifPresentOrElse(
                existing -> {
                    if (!bookmarked) {
                        bookmarks.delete(existing);
                    }
                },
                () -> {
                    if (bookmarked) {
                        Bookmark item = new Bookmark();
                        item.setUser(user);
                        item.setQuestion(question);
                        bookmarks.save(item);
                    }
                }
        );
        return map(question, email);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> bookmarks(String email) {
        User user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Bookmark> userBookmarks = bookmarks.findByUserId(user.getId());
        if (userBookmarks.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> questionIds = userBookmarks.stream()
                .map(b -> b.getQuestion().getId())
                .collect(Collectors.toSet());

        Set<Long> solvedIds = progress.findByUserIdAndQuestionIdInAndSolvedTrue(user.getId(), questionIds)
                .stream()
                .map(p -> p.getQuestion().getId())
                .collect(Collectors.toSet());

        return userBookmarks.stream()
                .map(item -> QuestionResponse.from(
                        item.getQuestion(),
                        true,
                        solvedIds.contains(item.getQuestion().getId())))
                .toList();
    }

    private QuestionResponse save(Question question, QuestionRequest request) {
        Topic topic = topics.findById(request.topicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        question.setQuestionText(request.questionText().trim());
        question.setAnswerText(request.answerText().trim());
        question.setDifficulty(request.difficulty());
        question.setTopic(topic);
        return QuestionResponse.from(questions.save(question), false, false);
    }

    private Question getEntity(Long id) {
        return questions.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
    }

    private PageResponse<QuestionResponse> mapPage(Page<Question> page, String email) {
        Set<Long> ids = page.getContent().stream().map(Question::getId).collect(Collectors.toSet());
        User user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Set<Long> bookmarked = ids.isEmpty()
                ? Collections.emptySet()
                : bookmarks.findByUserIdAndQuestionIdIn(user.getId(), ids)
                .stream().map(item -> item.getQuestion().getId()).collect(Collectors.toSet());
        Set<Long> solved = ids.isEmpty()
                ? Collections.emptySet()
                : progress.findByUserIdAndQuestionIdInAndSolvedTrue(user.getId(), ids)
                .stream().map(item -> item.getQuestion().getId()).collect(Collectors.toSet());

        return PageResponse.from(page, item -> QuestionResponse.from(
                item,
                bookmarked.contains(item.getId()),
                solved.contains(item.getId())));
    }

    private QuestionResponse map(Question question, String email) {
        User user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return QuestionResponse.from(
                question,
                bookmarks.findByUserIdAndQuestionId(user.getId(), question.getId()).isPresent(),
                progress.findByUserIdAndQuestionId(user.getId(), question.getId())
                        .map(UserQuestionProgress::isSolved).orElse(false));
    }
}
