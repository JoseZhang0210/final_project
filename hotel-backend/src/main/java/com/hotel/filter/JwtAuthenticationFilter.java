package com.hotel.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hotel.util.JwtUtils;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 注意：這裡不加上 @Component，完全當作一個普通的類別撰寫
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService; // 🔥 1. 注入您的 UserDetailsService

    // 建構子同步要求傳入兩者
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtUtils.validateToken(token)) {
                    String username = jwtUtils.extractUsername(token);

                    // 🔥 2. 關鍵：利用 Token 內的帳號，重新載入帶有完整資料庫權限的 UserDetails 物件
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Check if account is enabled before authenticating
                    if (userDetails.isEnabled()) {
                        // 🔥 3. 關鍵：將 userDetails.getAuthorities() 傳入第三個參數，權限才會在後端生效！
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (JwtException | UsernameNotFoundException e) {
                // Clear security context on JWT or user lookup failure
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
