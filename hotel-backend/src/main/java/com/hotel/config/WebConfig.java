package com.hotel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig
                implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(
                        ResourceHandlerRegistry registry) {

                registry
                                .addResourceHandler("/upload/**")
                                .addResourceLocations("file:upload/");

                // 讓前台與後台可以直接讀取圖片 (支援從 hotel-backend 內或外層啟動的兩種 CWD 情況)
                registry
                                .addResourceHandler("/uploads/**")
                                .addResourceLocations("file:uploads/", "file:hotel-backend/uploads/");
        }
}