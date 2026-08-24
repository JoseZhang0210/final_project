package com.hotel.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.hotel.filter.JwtAuthenticationFilter;
import com.hotel.util.JwtUtils;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

        private final JwtProperties jwtProperties;

        // =====================================================
        // Spring Security
        // =====================================================
        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity httpSecurity,
                        JwtAuthenticationFilter jwtAuthenticationFilter)
                        throws Exception {

                return httpSecurity

                                // =========================
                                // 啟用 CORS
                                // =========================
                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                // =========================
                                // JWT 不使用 CSRF
                                // =========================
                                .csrf(csrf -> csrf.disable())

                                // =========================
                                // JWT 不使用 Session
                                // =========================
                                .sessionManagement(
                                                session -> session
                                                                .sessionCreationPolicy(
                                                                                SessionCreationPolicy.STATELESS))

                                // =========================
                                // 權限設定
                                // =========================
                                .authorizeHttpRequests(
                                                requests -> requests

                                                                // -------------------------
                                                                // CORS 預檢請求
                                                                // -------------------------
                                                                .requestMatchers(
                                                                                HttpMethod.OPTIONS,
                                                                                "/**")
                                                                .permitAll()

                                                                // -------------------------
                                                                // 登入 / 註冊
                                                                // -------------------------
                                                                .requestMatchers(
                                                                                "/api/auth/**")
                                                                .permitAll()

                                                                // -------------------------
                                                                // Spring Boot error
                                                                // -------------------------
                                                                .requestMatchers(
                                                                                "/error")
                                                                .permitAll()

                                                                // -------------------------
                                                                // 其他 API
                                                                // 需要 JWT
                                                                // -------------------------
                                                                .anyRequest()
                                                                .authenticated())

                                // =========================
                                // JWT Filter
                                // =========================
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .build();
        }

        // =====================================================
        // CORS 設定
        // Vue localhost:5173
        // =====================================================
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                // Vue 前端
                configuration.setAllowedOrigins(
                                List.of(
                                                "http://localhost:5173"));

                // 允許 HTTP 方法
                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "OPTIONS"));

                // 允許 Header
                configuration.setAllowedHeaders(
                                List.of("*"));

                // 如果前端需要讀 Authorization Header
                configuration.setExposedHeaders(
                                List.of(
                                                "Authorization"));

                // 允許 credentials
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

        // =====================================================
        // Authentication Manager
        // =====================================================
        @Bean
        public AuthenticationManager authenticationManager(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(
                                userDetailsService);

                authenticationProvider.setPasswordEncoder(
                                passwordEncoder);

                return new ProviderManager(
                                authenticationProvider);
        }

        // =====================================================
        // JWT Utils
        // =====================================================
        @Bean
        public JwtUtils jwtUtils() {

                return new JwtUtils(
                                jwtProperties.getSecretKey(),
                                jwtProperties.getValidSeconds());
        }

        // =====================================================
        // JWT Filter
        // =====================================================
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter(
                        JwtUtils jwtUtils,
                        UserDetailsService userDetailsService) {

                return new JwtAuthenticationFilter(
                                jwtUtils,
                                userDetailsService);
        }

        // =====================================================
        // Password Encoder
        // =====================================================
        @Bean
        public PasswordEncoder passwordEncoder() {

                return new BCryptPasswordEncoder();
        }
}