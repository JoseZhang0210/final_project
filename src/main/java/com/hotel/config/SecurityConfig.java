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
						// 靜態檔案白名單 (HTML, CSS, JS)
						.requestMatchers("/", "/index.html", "/css/**", "/js/**", "/error").permitAll()

						// API 端點白名單 - 允許公開存取的 API
						.requestMatchers(HttpMethod.GET, "/api/home/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/products").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/venues").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/rentals").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/room-types").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll()

						// 其他 API 端點和操作需要認證
						.requestMatchers(HttpMethod.POST, "/api/**").authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

						// 認證端點
						.requestMatchers("/accounts/**").permitAll()

						// 任何其他請求都需要認證
						.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults())
				.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}