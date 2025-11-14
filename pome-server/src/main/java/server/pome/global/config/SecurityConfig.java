package server.pome.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.ignoringRequestMatchers(
            "/health/**", "health/check", "/actuator/**"
        ))
        .authorizeHttpRequests(auth -> auth
            // GET health check은 항상 통과하도록(security 제외)
            .requestMatchers(HttpMethod.GET,
                "/health/**", "/actuator/health", "/actuator/health/**")
            .permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
  }
}
