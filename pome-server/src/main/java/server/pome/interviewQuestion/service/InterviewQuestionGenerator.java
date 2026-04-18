package server.pome.interviewQuestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.User;
import server.pome.interviewQuestion.llm.InterviewLlmClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewQuestionGenerator {

    private final InterviewLlmClient llmClient;
    private final InterviewHistoryService interviewHistoryService;

    public List<String> generateQuestion(
            User user,
            String portfolio,
            List<String> similarQuestions
    ) {

        Long userId = user.getId();

        // history 조회
        List<String> historyQuestions =
                interviewHistoryService.getRecentQuestions(userId);

        List<String> contextQuestions = new ArrayList<>();
        contextQuestions.addAll(similarQuestions);
        contextQuestions.addAll(historyQuestions);

        // LLM 호출
        List<String> questions =
                llmClient.generateQuestion(portfolio, contextQuestions);

        interviewHistoryService.saveAll(user, questions);

        return questions;
    }
}
