package com.hotel.config;

import java.io.File;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 動態取得專案根目錄下 static/images/ 的絕對路徑
        File imagesDir = new File("src/main/resources/static/images");
        
        // 2. 如果目錄還不存在，自動幫團隊成員建立目錄結構
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        // 3. 轉為跨平台安全的 URI 路徑格式 (自動處理 Windows \ 與 Mac/Linux / 的差異)
        String imagesPath = imagesDir.toURI().toString();

        // 4. 將 /images/** (包含 /images/room/, /images/facility/ 等所有分類) 
        //    同時映射到：1) 本機真實硬碟目錄 2) Classpath 靜態資源
        registry.addResourceHandler("/images/**")
                .addResourceLocations(imagesPath, "classpath:/static/images/");
    }
}