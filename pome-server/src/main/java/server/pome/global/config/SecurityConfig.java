package server.pome.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import server.pome.user.auth.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    http.sessionManagement(
        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.csrf(AbstractHttpConfigurer::disable);

    http.formLogin(AbstractHttpConfigurer::disable);
    http.httpBasic(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(auth -> auth
            // 카카오 콜백 + 프론트에서 호출하는 로그인 API
            .requestMatchers("/users/login", "/users/login/**").permitAll()
            // 스웨거 / 에러 / 파비콘 등 기본 접근 허용
            .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/error",
                    "/favicon.ico"
            ).permitAll()

            // 테스트용
            .requestMatchers("/users/me").authenticated()

            .anyRequest().permitAll()
    );

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
