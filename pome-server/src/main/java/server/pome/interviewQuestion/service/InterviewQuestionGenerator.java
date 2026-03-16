package server.pome.interviewQuestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.interviewQuestion.llm.InterviewLlmClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewQuestionGenerator {

    // LLM 호출
    private final InterviewLlmClient llmClient;

    public String generateQuestion(
            String portfolio,
            List<String> similarQuestions
    ) {

        return llmClient.generateQuestion(
                portfolio,
                similarQuestions
        );
    }
}
