package com.hotel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 支援從專案根目錄 (final_project) 或子目錄 (hotel-backend) 啟動的多種 CWD 情況
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:uploads/",
                        "file:hotel-backend/uploads/",
                        "file:../uploads/"
                );

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(
                        "file:upload/",
                        "file:hotel-backend/upload/",
                        "file:../upload/"
                );
    }
}