package server.pome.global.config;

import java.util.concurrent.Semaphore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorIndexingConfig {

  @Bean
  public Semaphore vectorIndexingSemaphore(
      @Value("${vector-indexing.max-concurrency:1}") int maxConcurrency
  ) {
    return new Semaphore(Math.max(1, maxConcurrency));
  }
}
