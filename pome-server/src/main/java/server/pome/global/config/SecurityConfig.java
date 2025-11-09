package server.pome.global.config;

import static org.springframework.security.config.Customizer.withDefaults;

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
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/health/**", "/health/check", "/actuator/**", "/**"
            ))
        // CorsConfigurationSource 빈 사용
        .cors(withDefaults())

        // 3) 권한 규칙: 테스트 단계에선 전부 오픈
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // 프리플라이트
            .requestMatchers(HttpMethod.POST, "/file").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/file").permitAll()
            .requestMatchers("/actuator/**", "/health/**").permitAll()
            .anyRequest().permitAll()
        );

    return http.build();
  }
}
