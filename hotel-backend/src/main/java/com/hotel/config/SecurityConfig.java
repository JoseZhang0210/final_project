package com.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                                                                // .requestMatchers(HttpMethod.OPTIONS,
                                                                // "/**").permitAll()
                                                                // -------------------------
                                                                // 登入 / 註冊
                                                                // 不需要 JWT
                                                                // -------------------------
                                                                .requestMatchers("/api/auth/**").permitAll()
                                                                // -------------------------
                                                                // Spring Boot error
                                                                // -------------------------
                                                                .requestMatchers("/error").permitAll()
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