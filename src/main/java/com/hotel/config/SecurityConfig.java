package com.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(csrf -> csrf.disable())
                .formLogin(Customizer.withDefaults())
                .authorizeHttpRequests(requests -> requests
                // GET 白名單
                .requestMatchers(HttpMethod.GET,
                        "/",
                        "/register",
                        "/error",
                        "/*.html",
                        "/bookingorders/**",
                        "/static/**",
                        "/css/**",
                        "/js/**"
                ).permitAll()
                // POST 白名單
                .requestMatchers(HttpMethod.POST, "/accounts", "/bookingorders/**").permitAll()
                // 其餘需要驗證
                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    // @Bean
    // SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    //     return httpSecurity.csrf(csrf -> csrf.disable())
    //             .formLogin(Customizer.withDefaults())
    //             .authorizeHttpRequests(requests -> requests
    //             // Get 白名單
    //             .requestMatchers(HttpMethod.GET, "/", "/register", "/error").permitAll()
    //             // POST 白名單
    //             .requestMatchers(HttpMethod.POST, "/accounts", "/bookingorders/**").permitAll()
    //             .anyRequest().authenticated())
    //             .httpBasic(Customizer.withDefaults())
    //             .build();
    // }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
