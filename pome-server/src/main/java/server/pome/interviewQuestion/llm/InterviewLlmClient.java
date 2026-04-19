package server.pome.interviewQuestion.llm;

import java.util.List;

public interface InterviewLlmClient {

    List<String> generateQuestion(
            Long userId,
            String portfolio,
            List<String> similarQuestions
    );

}
