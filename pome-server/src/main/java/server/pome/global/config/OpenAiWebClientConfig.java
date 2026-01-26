package server.pome.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAiWebClientConfig {

  @Bean
  public WebClient openAiWebClient(
      @Value("${openai.base-url}") String baseUrl,
      @Value("${openai.api-key}") String apiKey
  ) {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
        .build();
  }
}
