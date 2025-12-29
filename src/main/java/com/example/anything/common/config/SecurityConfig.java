package com.example.anything.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // API 테스트를 위해 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger 관련 모든 경로 허용
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // 2. (선택) H2 콘솔 등을 사용한다면 추가
                        // .requestMatchers("/h2-console/**").permitAll()
                        // 3. 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // 인증되지 않은 사용자가 Swagger 접속 시 로그인 페이지로 가는 것을 방지
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
