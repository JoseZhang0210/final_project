package com.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Web 配置類 - 配置 CORS 以支持跨域前端請求
 */
@Configuration
public class WebConfig {

    /**
     * 配置 CORS 以允許前端 fetch/axios 跨域請求
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允許的來源 - 開發環境允許 localhost，生產環境應明確指定
        configuration.addAllowedOrigin("http://localhost:8082");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://127.0.0.1:8082");

        // 允許的 HTTP 方法
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");

        // 允許的請求標頭
        configuration.addAllowedHeader("*");
        configuration.addAllowedHeader("Content-Type");
        configuration.addAllowedHeader("Authorization");

        // 允許攜帶認證資訊（Cookie、授權標頭）
        configuration.setAllowCredentials(true);

        // 預檢請求的最大快取時間（秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
