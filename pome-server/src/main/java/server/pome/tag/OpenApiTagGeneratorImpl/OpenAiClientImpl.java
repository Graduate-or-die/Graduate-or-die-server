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
    if (limit <= 0) {
      throw new IllegalArgumentException("limit must be positive");
    }
    if (limit > 20) {
      throw new IllegalArgumentException("limit too large (max 20)");
    }
  }

  private int safeLen(String s) {
    return s == null ? 0 : s.length();
  }

  private String buildSystemPrompt(int limit) {
    int info = limit / 2;
    int humor = limit - info;

    return """
        너는 포트폴리오 태그 생성기다.
        목표는 포트폴리오를 읽고 "그 사람이 어떤 사람인지 빠르게 감이 오는 태그"를 만드는 것이다.

        출력 형식 규칙:
        - 반드시 JSON 객체 하나만 출력한다.
        - 형식은 정확히 {"tags":[...]} 이다.
        - tags 배열에는 정확히 %d개의 문자열만 넣는다.
        - 설명, 해설, 마크다운, 코드블록, 추가 문장은 절대 출력하지 않는다.

        태그 기본 규칙:
        - 모든 태그는 반드시 '#'으로 시작한다.
        - 모든 태그는 서로 중복되면 안 된다.
        - 태그는 짧고 직관적이어야 하며 보통 2~10자 정도로 만든다.
        - 한국어 중심으로 작성하되, 기술명/툴명/자격증명/고유명사는 필요할 때만 그대로 사용한다.
        - 입력 근거가 약한 정보는 추측해서 쓰지 않는다.
        - 식상한 태그만 반복하지 말고, 같은 의미라도 조금 더 선명하고 기억에 남게 만든다.

        태그 생성 절차:
        1. 포트폴리오에서 직무, 역할, 강점, 사용 기술, 결과물, 프로젝트 성격, 협업 방식, 성향을 먼저 파악한다.
        2. 그 내용을 바탕으로 정보 기반 태그를 만든다.
        3. 그 사람의 분위기나 일하는 방식이 느껴지는 위트 있는 태그를 만든다.
        4. 전체 태그를 봤을 때 너무 비슷한 의미가 겹치지 않게 정리한다.

        직군 해석 규칙:
        - 개발 직군에만 치우치지 말고 디자인, 기획, PM, 마케팅, 데이터, 운영, HR, 세일즈, 콘텐츠 직군도 적극 반영한다.
        - 한 사람에게 여러 역할이 보이면 대표 역할 1~2개와 보조 성향 태그를 조합한다.

        정보 기반 태그 작성 규칙:
        - 직무, 역할, 전문 분야, 기술 스택, 강점, 작업 방식, 결과물 중심으로 만든다.
        - 너무 넓은 표현보다 한 단계 구체적인 표현을 선호한다.
        - 예: "#개발자"보다 "#프론트엔드개발자", "#디자이너"보다 "#UX디자이너"
        - 예: "#마케터"보다 "#콘텐츠마케터", "#기획자"보다 "#서비스기획자"

        위트 태그 작성 규칙:
        - 포트폴리오 내용에 기반한 가벼운 캐릭터 표현만 허용한다.
        - 과장, 허위, 조롱, 혐오, 정치, 성적 표현은 금지한다.
        - 자기비하, 밈 남발, 의미 없는 유행어, 근거 없는 허세 표현은 금지한다.
        - 읽는 사람이 웃으면서도 "아 이 사람 느낌 알겠다" 수준이어야 한다.

        좋은 태그 예시:
        - 개발/데이터: "#프론트엔드개발자", "#백엔드개발자", "#데이터분석가", "#실험설계러", "#디버깅집착러"
        - 디자인: "#UX디자이너", "#UI디자이너", "#브랜드디자이너", "#사용자집착형", "#디테일수호자"
        - 기획/PM: "#서비스기획자", "#프로덕트매니저", "#문제정의장인", "#회의정리장인", "#프로젝트마스터"
        - 마케팅/콘텐츠: "#콘텐츠마케터", "#퍼포먼스마케터", "#트렌드캐처", "#카피한줄러", "#성과집착형"
        - 운영/비즈니스/기타: "#운영기획자", "#채용담당자", "#세일즈기획", "#조율능력자", "#마감수호자"

        피해야 하는 태그 예시:
        - 근거 부족: "#천재", "#업계최고", "#무조건합격"
        - 내용 중복: "#협업형", "#협업장인", "#협업중심"을 동시에 쓰는 것

        최종 구성 규칙:
        - 앞의 %d개는 정보 기반 태그로 만든다.
        - 뒤의 %d개는 위트 있는 태그로 만든다.
        - 태그끼리 결이 너무 비슷하면 하나는 다른 각도의 태그로 바꾼다.
        - 포트폴리오를 처음 보는 사람이 태그만 읽어도 직무와 캐릭터가 함께 떠오르게 만든다.
        """.formatted(limit, info, humor);
  }

  private String buildUserPrompt(String input) {
    return """
        아래 포트폴리오 내용을 읽고 태그를 생성하라.
        포트폴리오에 없는 내용은 만들어내지 마라.

        Portfolio content:
        %s
        """.formatted(input == null ? "" : input);
  }

  private List<String> parseTagObject(String content, int limit) {
    String trimmed = content.trim();
    String json = extractFirstJsonObject(trimmed);

    try {
      TagResponse resp = objectMapper.readValue(json, TagResponse.class);
      List<String> tags = resp.tags();
      if (tags == null) {
        throw new IllegalArgumentException("tags is null");
      }
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
    if (start < 0) {
      return text;
    }

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
      if (inString) {
        continue;
      }

      if (c == '{') {
        depth++;
      } else if (c == '}') {
        depth--;
        if (depth == 0) {
          return text.substring(start, i + 1);
        }
      }
    }
    return text;
  }
}
