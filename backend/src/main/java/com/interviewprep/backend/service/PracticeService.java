package com.interviewprep.backend.service;

import com.interviewprep.backend.entity.PracticeRecord;
import com.interviewprep.backend.entity.Question;
import com.interviewprep.backend.entity.User;
import com.interviewprep.backend.repository.PracticeRecordRepository;
import com.interviewprep.backend.repository.QuestionRepository;
import com.interviewprep.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PracticeService {

    private final PracticeRecordRepository practiceRecordRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public PracticeService(PracticeRecordRepository practiceRecordRepository, QuestionRepository questionRepository, UserRepository userRepository) {
        this.practiceRecordRepository = practiceRecordRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PracticeRecord> getCurrentUserHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return practiceRecordRepository.findByUserIdOrderByPracticedAtDesc(user.getId());
    }

    @Transactional
    public PracticeRecord recordPractice(Long questionId, PracticeRecord.PracticeType type, PracticeRecord.PracticeResult result, Integer score) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        PracticeRecord record = new PracticeRecord();
        record.setUser(user);
        record.setQuestion(question);
        record.setPracticeType(type);
        record.setResult(result);
        record.setScore(score);
        
        return practiceRecordRepository.save(record);
    }
}
