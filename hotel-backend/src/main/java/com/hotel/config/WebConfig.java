package com.hotel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 動態取得專案根目錄，並建立 uploads/images/room/ 實體資料夾
        String projectPath = System.getProperty("user.dir");
        String uploadDir = projectPath + File.separator + "uploads" + File.separator + "images" + File.separator + "room" + File.separator;
        
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 映射網址 /images/room/** 到專案內 static/images/room/ 資料夾
        registry.addResourceHandler("/images/room/**")
        .addResourceLocations("classpath:/static/images/room/");
    }

    // 開放 CORS 跨域請求（避免前端或跨port請求被檔）
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 核心修正：使用 allowedOriginPatterns
                .allowedOriginPatterns("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    

    
}