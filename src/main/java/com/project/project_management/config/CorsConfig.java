package com.project.project_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true) 
                
                // Cloudflare Pages 的访问地址
                // 如果本地调试也要用，就把 localhost 也加上
                .allowedOriginPatterns(
                    "https://project-management-blo.pages.dev", 
                    "http://localhost:5173"
                )
                
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
