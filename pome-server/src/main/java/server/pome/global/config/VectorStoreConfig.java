package server.pome.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.pome.vector.infrastructure.vectorstore.VectorStoreClient;
import server.pome.vector.infrastructure.vectorstore.qdrant.QdrantVectorStoreClient;

@Configuration
public class VectorStoreConfig {

  @Bean
  public VectorStoreClient vectorStoreClient(QdrantVectorStoreClient impl) {
    return impl;
  }
}
