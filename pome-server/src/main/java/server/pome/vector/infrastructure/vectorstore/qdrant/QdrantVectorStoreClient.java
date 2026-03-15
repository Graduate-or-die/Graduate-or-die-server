package server.pome.vector.infrastructure.vectorstore.qdrant;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import server.pome.vector.domain.Vector;
import server.pome.vector.infrastructure.vectorstore.VectorStoreClient;
import server.pome.vector.infrastructure.vectorstore.VectorStoreNamespace;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.QdrantPoint;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.QdrantRetrieveResponse;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.SearchHit;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.SearchRequest;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.SearchResponse;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.UpsertRequest;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.UpsertResponse;

@Component
@RequiredArgsConstructor
public class QdrantVectorStoreClient implements VectorStoreClient {

  private final WebClient webClient;
  private final QdrantClientFactory factory;

  @Value("${qdrant.timeout-ms}")
  private long timeoutMs;

  @Override
  public void upsert(
      VectorStoreNamespace ns,
      long pointId,
      List<Float> vector,
      Map<String, Object> payload
  ) {
    var request = new UpsertRequest(List.of(new QdrantPoint(pointId, vector, payload)));
    webClient.put()
        .uri(factory.getBaseUrl() + "/collections/" + ns.collection() + "/points?wait=true")
        .header("api-key", factory.getApiKey())
        .bodyValue(request)
        .retrieve()
        .bodyToMono(UpsertResponse.class)
        .timeout(Duration.ofMillis(timeoutMs))
        .block();
  }

  @Override
  public List<SearchHit> search(VectorStoreNamespace ns, List<Float> vector, int limit) {
    var request = new SearchRequest(vector, limit, false, false);
    SearchResponse response = webClient.post()
        .uri(factory.getBaseUrl() + "/collections/" + ns.collection() + "/points/search")
        .header("api-key", factory.getApiKey())
        .bodyValue(request)
        .retrieve()
        .bodyToMono(SearchResponse.class)
        .timeout(Duration.ofMillis(timeoutMs))
        .block();

    if (response == null || response.result() == null) {
      return List.of();
    }

    return response.result().stream()
        .map(hit -> new SearchHit(hit.id(), hit.score()))
        .toList();
  }

  @Override
  public Optional<List<Float>> getVector(VectorStoreNamespace ns, long pointId) {
    Map<String, Object> body = Map.of(
        "ids", List.of(pointId),
        "with_vector", true,
        "with_payload", false
    );

    QdrantRetrieveResponse response = webClient.post()
        .uri(factory.getBaseUrl() + "/collections/" + ns.collection() + "/points")
        .header("api-key", factory.getApiKey())
        .bodyValue(body)
        .retrieve()
        .bodyToMono(QdrantRetrieveResponse.class)
        .timeout(Duration.ofMillis(timeoutMs))
        .block();

    if (response == null || response.result() == null || response.result().isEmpty()) {
      return Optional.empty();
    }

    List<Float> vector = response.result().get(0).vector();
    new Vector(vector).validateDimension(ns);
    return Optional.of(vector);
  }
}
