package server.pome.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean @Order(0)
  SecurityFilterChain actuatorChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/actuator/**")
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())
        .csrf(c -> c.disable());
    return http.build();
  }

  // 일반 API 체인
  @Bean @Order(1)
  SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
    http.csrf(c -> c.disable())
        .authorizeHttpRequests(a -> a
            .requestMatchers("/swagger-ui/**","/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
  }
}
