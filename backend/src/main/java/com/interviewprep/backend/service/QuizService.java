package com.interviewprep.backend.service;

import com.interviewprep.backend.entity.*;
import com.interviewprep.backend.repository.PracticeRecordRepository;
import com.interviewprep.backend.repository.QuestionRepository;
import com.interviewprep.backend.repository.QuizAttemptRepository;
import com.interviewprep.backend.repository.QuizRepository;
import com.interviewprep.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PracticeRecordRepository practiceRecordRepository;

    public QuizService(QuizRepository quizRepository, QuizAttemptRepository quizAttemptRepository,
                       UserRepository userRepository, QuestionRepository questionRepository,
                       PracticeRecordRepository practiceRecordRepository) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.practiceRecordRepository = practiceRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    @Transactional
    public QuizAttempt startQuiz(Long quizId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStatus(QuizAttempt.AttemptStatus.IN_PROGRESS);
        attempt.setTotalQuestions(quiz.getQuestions().size());

        return quizAttemptRepository.save(attempt);
    }

    @Transactional
    public QuizAttempt submitQuiz(Long attemptId, List<Long> selectedOptionIds) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to quiz attempt");
        }

        Quiz quiz = attempt.getQuiz();
        List<Question> questions = quiz.getQuestions();

        int correctCount = 0;
        for (int i = 0; i < questions.size() && i < selectedOptionIds.size(); i++) {
            Question question = questions.get(i);
            if (question.getCorrectOption() != null && 
                question.getCorrectOption().ordinal() == selectedOptionIds.get(i).intValue()) {
                correctCount++;
            }
        }

        int score = questions.size() > 0 ? (correctCount * 100) / questions.size() : 0;

        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setStatus(QuizAttempt.AttemptStatus.COMPLETED);
        attempt.setScore(score);
        attempt.setCorrectAnswers(correctCount);

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        // Record practice history
        PracticeRecord record = new PracticeRecord();
        record.setUser(user);
        record.setPracticeType(PracticeRecord.PracticeType.QUIZ);
        record.setResult(PracticeRecord.PracticeResult.COMPLETED);
        record.setScore(score);
        practiceRecordRepository.save(record);

        return savedAttempt;
    }

    @Transactional(readOnly = true)
    public List<QuizAttempt> getCurrentUserAttempts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return quizAttemptRepository.findByUserIdOrderByCompletedAtDesc(user.getId());
    }

    @Transactional(readOnly = true)
    public List<QuizAttempt> getAttemptsForQuiz(Long quizId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return quizAttemptRepository.findByQuizIdAndUserId(quizId, user.getId());
    }

    @Transactional
    public Quiz createQuiz(Quiz quiz, Long topicId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (topicId != null) {
            Topic topic = new Topic();
            topic.setId(topicId);
            quiz.setTopic(topic);
        }
        quiz.setCreatedBy(user);
        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz updateQuiz(Long quizId, Quiz updatedQuiz) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        existingQuiz.setTitle(updatedQuiz.getTitle());
        existingQuiz.setDescription(updatedQuiz.getDescription());
        existingQuiz.setDifficulty(updatedQuiz.getDifficulty());
        existingQuiz.setTimeLimitMinutes(updatedQuiz.getTimeLimitMinutes());
        existingQuiz.setActive(updatedQuiz.getActive());

        if (updatedQuiz.getTopic() != null) {
            existingQuiz.setTopic(updatedQuiz.getTopic());
        }

        return quizRepository.save(existingQuiz);
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
    }
}
