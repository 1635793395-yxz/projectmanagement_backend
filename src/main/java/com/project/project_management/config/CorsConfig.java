package com.project.project_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true) // 允许前端带 Cookie
                .allowedOriginPatterns(
                    "https://xmgl.lgyt304.shop",       // 你的自定义域名
                    "https://*.lgyt304.shop",          // 或者是通配符
                    "https://*.pages.dev",             // 兼容旧的 Cloudflare 地址
                    "https://*.onrender.com",          // 兼容 Render 自身
                    "http://localhost:5173",           // 本地开发
                    "http://localhost:8080"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
