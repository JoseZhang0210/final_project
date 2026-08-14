package com.hotel.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(csrf -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.formLogin(formLogin -> formLogin
						.loginPage("/login.html")
						.defaultSuccessUrl("/index.html", true)
						.permitAll())
				.authorizeHttpRequests(requests -> requests
						// 靜態檔案白名單 (HTML, CSS, JS)
						.requestMatchers("/", "/index.html", "/login.html", "/register.html", "/css/**", "/js/**", "/error").permitAll()

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
				.addFilterAfter(csrfCookieFilter(), CsrfFilter.class)
				.build();
	}

	private OncePerRequestFilter csrfCookieFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
				CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if (csrfToken != null) {
					csrfToken.getToken();
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
