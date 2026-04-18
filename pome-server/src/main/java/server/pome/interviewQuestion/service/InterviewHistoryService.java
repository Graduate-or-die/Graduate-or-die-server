package server.pome.interviewQuestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.InterviewQuestionHistory;
import server.pome.global.domain.User;
import server.pome.interviewQuestion.repository.InterviewQuestionHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewHistoryService {


    private final InterviewQuestionHistoryRepository repository;

    // 최근 20개 조회 (LLM 입력용)
    public List<String> getRecentQuestions(Long userId) {
        return repository.findTop20ByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(InterviewQuestionHistory::getQuestionText)
                .toList();

    }

    // 생성 질문 저장
    public void saveAll(User user, List<String> questions) {
        List<InterviewQuestionHistory> entities =
                questions.stream()
                        .map(q -> InterviewQuestionHistory.builder()
                                .user(user)
                                .questionText(q)
                                .build()
                        )
                        .toList();

        try {

            repository.saveAll(entities);

        } catch (Exception ignored) {

        }

    }
}
