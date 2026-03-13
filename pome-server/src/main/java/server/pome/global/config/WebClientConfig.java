package server.pome.global.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient webClient(
      @Value("${embedding.timeout-ms:2000}") long embeddingTimeoutMs,
      @Value("${qdrant.timeout-ms:2000}") long qdrantTimeoutMs
  ) {
    long timeoutMs = Math.max(embeddingTimeoutMs, qdrantTimeoutMs);

    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutMs)
        .responseTimeout(Duration.ofMillis(timeoutMs));

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
