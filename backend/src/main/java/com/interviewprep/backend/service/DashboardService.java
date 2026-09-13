package com.interviewprep.backend.service;

import com.interviewprep.backend.entity.*;
import com.interviewprep.backend.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final NoteRepository noteRepository;
    private final PracticeRecordRepository practiceRecordRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserQuestionProgressRepository userQuestionProgressRepository;
    private final TopicRepository topicRepository;

    public DashboardService(UserRepository userRepository, QuestionRepository questionRepository,
                            BookmarkRepository bookmarkRepository, NoteRepository noteRepository,
                            PracticeRecordRepository practiceRecordRepository,
                            QuizAttemptRepository quizAttemptRepository,
                            UserQuestionProgressRepository userQuestionProgressRepository,
                            TopicRepository topicRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.noteRepository = noteRepository;
        this.practiceRecordRepository = practiceRecordRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userQuestionProgressRepository = userQuestionProgressRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        List<Note> notes = noteRepository.findByUserId(userId);
        List<PracticeRecord> practiceRecords = practiceRecordRepository.findByUserIdOrderByPracticedAtDesc(userId);
        List<QuizAttempt> quizAttempts = quizAttemptRepository.findByUserIdOrderByCompletedAtDesc(userId);
        List<UserQuestionProgress> progressList = userQuestionProgressRepository.findByUserId(userId);

        long totalQuestions = questionRepository.count();
        long solvedCount = progressList.stream()
                .filter(p -> p.isSolved())
                .count();
        long unsolvedCount = totalQuestions - solvedCount;
        long bookmarkedCount = bookmarks.size();
        long notesCount = notes.size();
        long practiceSessions = practiceRecords.size();
        long quizAttemptsCount = quizAttempts.size();

        double averageQuizScore = quizAttempts.stream()
                .filter(a -> a.getScore() != null)
                .mapToInt(QuizAttempt::getScore)
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuestions", totalQuestions);
        stats.put("solvedQuestions", solvedCount);
        stats.put("unsolvedQuestions", unsolvedCount);
        stats.put("bookmarkedQuestions", bookmarkedCount);
        stats.put("notesCount", notesCount);
        stats.put("practiceSessions", practiceSessions);
        stats.put("quizAttempts", quizAttemptsCount);
        stats.put("averageQuizScore", Math.round(averageQuizScore * 100.0) / 100.0);

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTopicProgress() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();
        List<UserQuestionProgress> progressList = userQuestionProgressRepository.findByUserId(userId);

        List<Topic> topics = topicRepository.findAll();
        List<Map<String, Object>> topicProgress = topics.stream().map(topic -> {
            Map<String, Object> topicData = new HashMap<>();
            topicData.put("topicId", topic.getId());
            topicData.put("topicName", topic.getName());

            List<Question> topicQuestions = questionRepository.findByTopicId(topic.getId());
            long totalInTopic = topicQuestions.size();

            List<Long> topicQuestionIds = topicQuestions.stream()
                    .map(Question::getId)
                    .collect(Collectors.toList());

            long solvedInTopic = progressList.stream()
                    .filter(p -> p.getQuestion() != null && 
                                 topicQuestionIds.contains(p.getQuestion().getId()) &&
                                 p.isSolved())
                    .count();

            topicData.put("totalQuestions", totalInTopic);
            topicData.put("solvedQuestions", solvedInTopic);
            topicData.put("progressPercentage", totalInTopic > 0 ? 
                    Math.round((solvedInTopic * 100.0) / totalInTopic) : 0);

            return topicData;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("topics", topicProgress);
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRecentActivity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        List<PracticeRecord> recentPractice = practiceRecordRepository
                .findByUserIdOrderByPracticedAtDesc(userId)
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        List<QuizAttempt> recentQuizAttempts = quizAttemptRepository
                .findByUserIdOrderByCompletedAtDesc(userId)
                .stream()
                .filter(a -> a.getCompletedAt() != null)
                .limit(5)
                .collect(Collectors.toList());

        List<Map<String, Object>> activities = recentPractice.stream().map(record -> {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "PRACTICE");
            activity.put("id", record.getId());
            activity.put("questionText", record.getQuestion() != null ? 
                    record.getQuestion().getQuestionText() : null);
            activity.put("topicName", record.getQuestion() != null && 
                    record.getQuestion().getTopic() != null ? 
                    record.getQuestion().getTopic().getName() : null);
            activity.put("practiceType", record.getPracticeType());
            activity.put("result", record.getResult());
            activity.put("score", record.getScore());
            activity.put("timestamp", record.getPracticedAt());
            return activity;
        }).collect(Collectors.toList());

        List<Map<String, Object>> quizActivities = recentQuizAttempts.stream().map(attempt -> {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "QUIZ");
            activity.put("id", attempt.getId());
            activity.put("quizTitle", attempt.getQuiz() != null ? 
                    attempt.getQuiz().getTitle() : null);
            activity.put("score", attempt.getScore());
            activity.put("totalQuestions", attempt.getTotalQuestions());
            activity.put("correctAnswers", attempt.getCorrectAnswers());
            activity.put("timestamp", attempt.getCompletedAt());
            return activity;
        }).collect(Collectors.toList());

        activities.addAll(quizActivities);
        activities.sort((a1, a2) -> {
            LocalDateTime t1 = (LocalDateTime) a1.get("timestamp");
            LocalDateTime t2 = (LocalDateTime) a2.get("timestamp");
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("activities", activities);
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFullDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("stats", getDashboardStats());
        dashboard.put("topicProgress", getTopicProgress());
        dashboard.put("recentActivity", getRecentActivity());
        return dashboard;
    }
}
