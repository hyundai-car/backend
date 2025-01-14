package com.myme.mycarforme.global.config.security;

import java.util.Arrays;
import java.util.List;

import com.myme.mycarforme.global.config.keycloak.KeycloakJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public KeycloakJwtAuthenticationConverter jwtAuthenticationConverter() {
        return new KeycloakJwtAuthenticationConverter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors
                    .configurationSource(corsConfigurationSource())
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    )
            )
            // OAuth2 Login (관리자 페이지용)
            .oauth2Login(oauth2 -> oauth2
                    .defaultSuccessUrl("/admin/dashboard", true)
            )
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/auth/login")
                        .permitAll()
                        .requestMatchers("/api/**")
                        .hasRole("MEMBER")
                        .requestMatchers("/", "/login", "/error", "/assets/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated();
            });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://mycarf0r.me"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
