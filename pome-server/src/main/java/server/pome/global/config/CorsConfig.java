package server.pome.global.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var c = new CorsConfiguration();

    c.setAllowedOrigins(List.of(
        "http://localhost:8080",
        "http://alb-spring-722955680.ap-northeast-2.elb.amazonaws.com",
        "http://localhost:3000",
        "http://localhost:5173"));
    c.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
    c.setAllowedHeaders(List.of("*"));
    c.setExposedHeaders(List.of("Location", "Content-Disposition"));
    c.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", c);
    return source;
  }

}
