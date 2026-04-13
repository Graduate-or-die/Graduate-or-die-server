package server.pome.interviewQuestion.llm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.interviewQuestion.service.QuestionFilterService;
import server.pome.tag.dto.request.ChatCompletionRequest;
import server.pome.tag.dto.response.ChatCompletionResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewLlmClientImpl implements InterviewLlmClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);

    private final WebClient openAiWebClient;
    private final QuestionFilterService questionFilterService;

    @Override
    public List<String> generateQuestion(String portfolio, List<String> similarQuestions) {

        final int TARGET_COUNT = 7;
        final int MAX_RETRY = 2;

        List<String> finalQuestions = new ArrayList<>();
        int attempt = 0;

        while (attempt <= MAX_RETRY) {

        String systemPrompt = buildSystemPrompt(7);
        String userPrompt = buildUserPrompt(portfolio, similarQuestions);

        ChatCompletionRequest req = new ChatCompletionRequest(
                "gpt-5.1-chat-latest",
                List.of(
                        new ChatCompletionRequest.Message("system", systemPrompt),
                        new ChatCompletionRequest.Message("user", userPrompt)
                ),
                null
        );

        ChatCompletionResponse resp = openAiWebClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .timeout(TIMEOUT)
                .block();

            if (resp == null || resp.choices().isEmpty()) {
                throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
            }

        List<String> questions = parseJsonArray(resp.choices().get(0).message().content());

        List<String> filteredQuestions =
                questionFilterService.filterWithHistory(
                        questions,
                        similarQuestions
                );

        finalQuestions.addAll(filteredQuestions);

        if (finalQuestions.size() >= TARGET_COUNT) {
            break;
        }

        attempt++;
    }
        
    return finalQuestions.stream()
            .distinct()
            .limit(TARGET_COUNT)
            .toList();

    }

    // 역할 정의
    private String buildSystemPrompt(int questionCount) {

        return """
    너는 전문 면접관이다.

    지원자의 포트폴리오, 경험, 활동 등을 바탕으로
    실제 면접에서 사용할 수 있는 맞춤형 면접 질문을 생성한다.
    
    질문 생성 목표:
    - 지원자의 경험, 프로젝트, 활동, 사고방식 등을 깊이 있게 탐색할 수 있는 질문을 만든다.
    - 질문은 실제 면접 상황에서 면접관이 물을 법한 자연스러운 질문이어야 한다.

    질문 생성 규칙:
    - 정확히 %d개의 면접 질문을 생성한다.
    - 모든 질문은 서로 중복되지 않아야 한다.
    - 질문은 지원자의 경험이나 활동, 프로젝트 등을 기반으로 해야 한다.
    - 지나치게 일반적인 질문은 최소화한다.
      예: "자기소개 해주세요", "지원 동기는 무엇인가요"
    - 질문은 한 줄 문장으로 작성한다.

    질문 유형 가이드:
    생성되는 질문에는 다음과 같은 유형이 다양하게 포함되도록 한다.
    
    - 경험 기반 질문
    - 문제 해결 경험 질문
    - 협업 또는 커뮤니케이션 질문
    - 실패 또는 어려움을 극복한 질문
    - 의사결정 과정에 대한 질문
    - 성장 또는 학습 경험 질문

    참고 규칙:
    - 제공되는 참고 질문들은 단지 참고용 예시이다.
    - 참고 질문을 그대로 복사하거나 단순히 변형해서는 안 된다.
    - 지원자의 포트폴리오 내용을 반영하여 새로운 질문을 만들어야 한다.

    출력 형식 규칙:
    - 반드시 JSON 배열만 출력한다.
    - 다른 설명이나 문장, 마크다운은 절대 출력하지 않는다.
    
    출력 예시:
    ["질문1", "질문2", "질문3"]
    """.formatted(questionCount);
    }

    // 실제 입력 데이터
    private String buildUserPrompt(
            String portfolio,
            List<String> referenceQuestions
    ) {

        return """
    지원자의 포트폴리오 정보는 다음과 같다.

    Portfolio:
    %s

    아래 질문들은 다양한 면접에서 사용되는 "참고용 질문 예시"이다.

    Reference interview questions:
    %s

    위 질문들은 참고용 예시일 뿐이며,
    질문을 그대로 복사하거나 단순히 변형해서는 안 된다.

    포트폴리오에 나타난 경험, 활동, 프로젝트 등을 기반으로
    지원자에게 적합한 새로운 면접 질문들을 생성하라.
    """.formatted(
                portfolio,
                referenceQuestions.stream().map(q -> "- " + q).reduce("", (a,b) -> a + "\n" + b)
        );
    }

    private List<String> parseJsonArray(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            content = content.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            int start = content.indexOf("[");
            int end = content.lastIndexOf("]");

            if (start != -1 && end != -1) {
                content = content.substring(start, end + 1);
            }

            return objectMapper.readValue(
                    content,
                    new TypeReference<List<String>>() {}
            );

        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }
}
