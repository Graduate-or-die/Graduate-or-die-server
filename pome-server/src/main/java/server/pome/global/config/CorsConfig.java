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
  public CorsConfigurationSource corsConfigurationSource() {
    var c = new CorsConfiguration();
    c.setAllowedOrigins(List.of(
        "http://localhost:3000",
        "http://localhost:5173",
        "http://alb-spring-722955680.ap-northeast-2.elb.amazonaws.com"
    ));
    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("*"));
    c.setExposedHeaders(List.of("Location","Content-Disposition"));
    c.setAllowCredentials(true);
    c.setMaxAge(3600L);

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", c);
    return source;
  }
}

