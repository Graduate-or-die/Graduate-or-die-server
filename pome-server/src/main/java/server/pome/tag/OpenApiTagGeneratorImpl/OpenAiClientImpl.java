package server.pome.tag.OpenApiTagGeneratorImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import server.pome.tag.OpenApiTagGenerator.OpenAiClient;
import server.pome.tag.dto.request.ChatCompletionRequest;
import server.pome.tag.dto.response.ChatCompletionResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClientImpl implements OpenAiClient {

  private static final Duration TIMEOUT = Duration.ofSeconds(15);

  private final WebClient openAiWebClient;
  private final ObjectMapper objectMapper;

  @Override
  public List<String> generateTags(String input, int limit) {
    validateLimit(limit);

    String system = buildSystemPrompt(limit);
    String user = buildUserPrompt(input);

    var req = new ChatCompletionRequest(
        "gpt-5.1-chat-latest",
        List.of(
            new ChatCompletionRequest.Message("system", system),
            new ChatCompletionRequest.Message("user", user)
        ),
        null
    );

    log.info("[TAG_AI] OpenAI call start limit={}, inputLen={}", limit, safeLen(input));

    ChatCompletionResponse resp = openAiWebClient.post()
        .uri("/chat/completions")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(req)
        .retrieve()
        .onStatus(
            status -> status.isError(),
            clientResponse -> clientResponse.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> {
                  log.error("[TAG_AI] OpenAI error status={}, body={}", clientResponse.statusCode(), body);
                  return Mono.error(new RuntimeException("OpenAI error"));
                })
        )
        .bodyToMono(ChatCompletionResponse.class)
        .timeout(TIMEOUT)
        .doOnSuccess(r -> log.info("[TAG_AI] OpenAI call success"))
        .doOnError(e -> log.warn("[TAG_AI] OpenAI tag generation failed", e))
        .block();

    if (resp == null || resp.choices() == null || resp.choices().isEmpty()
        || resp.choices().get(0) == null || resp.choices().get(0).message() == null) {
      throw new RuntimeException("OpenAI empty response");
    }

    String content = resp.choices().get(0).message().content();
    if (content == null || content.isBlank()) {
      throw new RuntimeException("OpenAI empty content");
    }

    return parseTagObject(content, limit);
  }

  private void validateLimit(int limit) {
    if (limit <= 0) throw new IllegalArgumentException("limit must be positive");
    if (limit > 20) throw new IllegalArgumentException("limit too large (max 20)");
  }

  private int safeLen(String s) {
    return s == null ? 0 : s.length();
  }

  private String buildSystemPrompt(int limit) {
    int info = limit / 2;
    int humor = limit - info;

    return """
        너는 포트폴리오 태그 생성기다.

        출력 형식 규칙(반드시 지킬 것):
        - 출력은 반드시 JSON 객체 하나만 출력한다.
        - 형식은 정확히 다음과 같다: {"tags":[...]}
        - tags 배열에는 정확히 %d개의 문자열만 들어간다.
        - 다른 필드, 설명, 문장, 마크다운을 절대 출력하지 않는다.

        태그 작성 규칙:
        - 모든 태그는 '#'으로 시작한다. 예: "#백엔드"
        - 태그는 중복 없이 모두 달라야 한다.
        - 태그는 한국어 중심으로 작성한다.
          - 자격증명/기술명/고유명사는 원문 유지 가능 (예: Spring Boot, AWS, 정보처리기사)
        - 길이는 짧게 유지한다. (권장 2~10자, 문장 금지)

        구성(정확히 지킬 것):
        - tags 배열의 앞 %d개: 정보 기반 태그
          - 기술, 도메인, 직무, 경험, 산출물, 자격증 등 객관적 내용
        - tags 배열의 뒤 %d개: 유머러스/위트 태그
          - 포트폴리오 맥락에서 자연스러운 가벼운 말장난/밈
          - 자기비하, 혐오, 성적, 정치적 표현 금지
          - 과한 허세 금지 ("천재", "세계최강" 등)
          - 납득 불가능한 뜬금포 유머 금지

        입력으로 주어지는 포트폴리오 내용에 근거해 태그를 생성하라.
        """.formatted(limit, info, humor);
  }

  private String buildUserPrompt(String input) {
    return "Portfolio content:\n" + (input == null ? "" : input);
  }

  private List<String> parseTagObject(String content, int limit) {
    String trimmed = content.trim();

    // JSON 객체만 추출하는 fallback
    String json = extractFirstJsonObject(trimmed);

    try {
      TagResponse resp = objectMapper.readValue(json, TagResponse.class);
      List<String> tags = resp.tags();
      if (tags == null) throw new IllegalArgumentException("tags is null");

      // 개수/형식/중복 검증
      if (tags.size() != limit) {
        throw new IllegalArgumentException("tag size mismatch. expected=" + limit + ", actual=" + tags.size());
      }
      for (String t : tags) {
        if (t == null || !t.startsWith("#") || t.length() < 2) {
          throw new IllegalArgumentException("invalid tag: " + t);
        }
      }

      return tags;
    } catch (Exception e) {
      log.error("[TAG_AI] Invalid tag JSON. content={}", trimmed);
      throw new RuntimeException("Invalid tag JSON", e);
    }
  }

  private record TagResponse(List<String> tags) {}

  private String extractFirstJsonObject(String text) {
    int start = text.indexOf('{');
    if (start < 0) return text;

    int depth = 0;
    boolean inString = false;
    boolean escape = false;

    for (int i = start; i < text.length(); i++) {
      char c = text.charAt(i);

      if (escape) {
        escape = false;
        continue;
      }
      if (c == '\\') {
        escape = true;
        continue;
      }
      if (c == '"') {
        inString = !inString;
        continue;
      }
      if (inString) continue;

      if (c == '{') depth++;
      else if (c == '}') {
        depth--;
        if (depth == 0) {
          return text.substring(start, i + 1);
        }
      }
    }
    return text;
  }
}
