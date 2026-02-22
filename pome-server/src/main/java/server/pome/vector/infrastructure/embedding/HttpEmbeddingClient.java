package server.pome.vector.infrastructure.embedding;

import static server.pome.global.exception.BaseResponseStatus.INVALID_EMBEDDING_RESPONSE;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import server.pome.global.exception.BaseException;
import server.pome.vector.infrastructure.embedding.dto.EmbeddingRequest;
import server.pome.vector.infrastructure.embedding.dto.EmbeddingResponse;

@RequiredArgsConstructor
public class HttpEmbeddingClient implements EmbeddingClient {

  private final WebClient webClient;
  @Value("${embedding.base-url}")
  private String baseUrl;
  @Value("${embedding.timeout-ms:2000}")
  private long timeoutMs;
  @Value("${embedding.retry.max-attempts:2}")
  private int maxAttempts;
  @Value("${embedding.retry.backoff-ms:200}")
  private long backOffMs;

  @Override
  public List<Float> embed(String text) {
    EmbeddingResponse response = webClient.post()
        .uri(baseUrl + "/embed")
        .bodyValue(new EmbeddingRequest(text))
        .retrieve()
        .bodyToMono(EmbeddingResponse.class)
        .timeout(Duration.ofMillis(timeoutMs))
        .retryWhen(Retry.backoff(Math.max(0, maxAttempts - 1), Duration.ofMillis(backOffMs)))
        .block();

    if (response == null || response.vector() == null || response.size() != 384) {
      throw new BaseException(INVALID_EMBEDDING_RESPONSE);
    }

    return IntStream.range(0, response.vector().length).mapToObj(i -> response.vector()[i])
        .toList();
  }
}
