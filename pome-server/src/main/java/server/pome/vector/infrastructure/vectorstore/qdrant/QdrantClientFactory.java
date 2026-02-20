package server.pome.vector.infrastructure.vectorstore.qdrant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QdrantClientFactory {

  @Getter
  @Value("${qdrant.url}")
  private final String baseUrl;

  @Getter
  @Value("${qdrant.api-key}")
  private final String apiKey;
}
