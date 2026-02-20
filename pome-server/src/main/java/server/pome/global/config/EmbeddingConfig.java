package server.pome.global.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.pome.vector.infrastructure.embedding.EmbeddingClient;
import server.pome.vector.infrastructure.embedding.HttpEmbeddingClient;
import server.pome.vector.infrastructure.embedding.MockEmbeddingClient;

@Configuration
public class EmbeddingConfig {

  @Bean
  @ConditionalOnProperty(name = "embedding.mock", havingValue = "true")
  public EmbeddingClient mockEmbeddingClient() {
    return new MockEmbeddingClient();
  }

  @Bean
  @ConditionalOnProperty(name = "embedding.mock", havingValue = "false", matchIfMissing = true)
  public EmbeddingClient httpEmbeddingClient(HttpEmbeddingClient client) {
    return client;
  }
}
