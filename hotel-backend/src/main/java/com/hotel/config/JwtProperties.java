package com.hotel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt") // 💡 告訴 Spring 這類別對應所有 jwt. 開頭的設定
public class JwtProperties {
    private String secretKey;
    private int validSeconds;
}
