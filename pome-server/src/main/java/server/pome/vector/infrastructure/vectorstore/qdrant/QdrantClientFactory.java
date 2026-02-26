package server.pome.vector.infrastructure.vectorstore.qdrant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QdrantClientFactory {

  @Getter
  @Value("${qdrant.url}")
  private String baseUrl;

  @Getter
  @Value("${qdrant.api-key}")
  private String apiKey;
}
