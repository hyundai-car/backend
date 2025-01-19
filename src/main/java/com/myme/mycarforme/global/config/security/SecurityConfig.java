package com.myme.mycarforme.global.config.security;

import com.myme.mycarforme.global.config.keycloak.KeycloakOidcUserService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.myme.mycarforme.global.config.keycloak.KeycloakJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final KeycloakJwtAuthenticationConverter jwtAuthenticationConverter;
    private final KeycloakOidcUserService keycloakOidcUserService;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUrl;

    @Value("${app.base.url}")
    private String baseUrl;

    @Bean
    @Order(1)  // API 요청 처리를 먼저
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**", "/ws/**")  // API, WebSocket 경로에만 적용
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .securityContext(context -> context
                        .requireExplicitSave(false)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/ws/**", "/ws/tracking/**")   // 웹소켓 허용 (인증은 AuthChannelInterceptor에서 별도 진행)
                            .permitAll()
                            .requestMatchers("/api/auth/login", "/api/auth/reissue")    // 로그인/재발급 허용
                            .permitAll()
                            .requestMatchers("/api/cars/**", "/api/recommendations/**", "api/likes/**", "/api/orders/**", "/api/visits/**", "/api/fcm/**")
                            .hasRole("MEMBER")
                            .requestMatchers("/api/admins/**")
                            .hasRole("ADMIN")
                            .anyRequest()
                            .authenticated();
                });

        return http.build();
    }

    @Bean
    @Order(2)  // 웹 페이지 요청은 그 다음에 처리
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**","/swagger-ui/**", "/v3/api-docs/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .loginPage("/admin/oauth2/authorization/keycloak-admin")
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/admin/oauth2/authorization"))  // 인가 엔드포인트 변경
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/admin/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(keycloakOidcUserService)
                        )
                )
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                            .permitAll()
                            .requestMatchers("/admin/login", "/admin/oauth2/**", "/admin/login/oauth2/code/**")
                            .permitAll()
                            .requestMatchers("/admin/**")
                            .hasRole("ADMIN")
                            .anyRequest()
                            .authenticated();
                })
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (!(authentication instanceof OAuth2AuthenticationToken)) {
                                response.sendRedirect(baseUrl + "/admin/oauth2/authorization/keycloak-admin");
                                return;
                            }

                            // 현재 인증된 사용자의 ID 토큰 가져오기
                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                            OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
                            String idToken = oidcUser.getIdToken().getTokenValue();

                            String keycloakLogoutUrl = issuerUrl + "/protocol/openid-connect/logout"
                                    + "?post_logout_redirect_uri="
                                    + URLEncoder.encode(baseUrl + "/admin/oauth2/authorization/keycloak-admin", StandardCharsets.UTF_8)
                                    + "&id_token_hint=" + idToken;
                            response.sendRedirect(keycloakLogoutUrl);
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://mycarf0r.me", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
