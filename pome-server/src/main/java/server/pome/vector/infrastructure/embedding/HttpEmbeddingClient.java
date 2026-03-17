package server.pome.vector.infrastructure.embedding;

import static server.pome.global.exception.BaseResponseStatus.INVALID_EMBEDDING_RESPONSE;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import server.pome.global.exception.BaseException;
import server.pome.vector.infrastructure.embedding.dto.EmbeddingRequest;
import server.pome.vector.infrastructure.embedding.dto.EmbeddingResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpEmbeddingClient implements EmbeddingClient {

  private final WebClient webClient;
  @Value("${embedding.base-url}")
  private String baseUrl;
  @Value("${embedding.vector-size}")
  private int vectorSize;
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
        .exchangeToMono(clientResponse -> {
          HttpStatusCode status = clientResponse.statusCode();
          if (status.is2xxSuccessful()) {
            return clientResponse.bodyToMono(EmbeddingResponse.class);
          }

          return clientResponse.bodyToMono(String.class)
              .defaultIfEmpty("")
              .flatMap(body -> {
                log.error("Embedding API request failed. status={}, baseUrl={}, body={}",
                    status.value(), baseUrl, body);
                return clientResponse.createException().flatMap(reactor.core.publisher.Mono::error);
              });
        })
        .timeout(Duration.ofMillis(timeoutMs))
        .retryWhen(Retry.backoff(Math.max(0, maxAttempts - 1), Duration.ofMillis(backOffMs)))
        .block();

    if (response == null || response.vector() == null || response.size() != vectorSize
        || response.vector().length != vectorSize) {
      log.error("Embedding API returned invalid payload. baseUrl={}, expectedSize={}, actualSize={}, vectorLength={}",
          baseUrl, vectorSize, response == null ? null : response.size(),
          response == null || response.vector() == null ? null : response.vector().length);
      throw new BaseException(INVALID_EMBEDDING_RESPONSE);
    }

    return IntStream.range(0, response.vector().length).mapToObj(i -> response.vector()[i])
        .toList();
  }
}
