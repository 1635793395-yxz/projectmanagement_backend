package com.project.project_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有的路径
        registry.addMapping("/**")
                // 允许所有的来源
                .allowedOriginPatterns("*")
                // 允许所有的 HTTP 方法 (GET, POST, PUT, DELETE, OPTIONS)
                .allowedMethods("*")
                // 允许携带凭证 (如 Cookies)
                .allowCredentials(true)
                // 预检请求的缓存时间 (秒)
                .maxAge(3600)
                // 允许所有的请求头
                .allowedHeaders("*");
    }
}