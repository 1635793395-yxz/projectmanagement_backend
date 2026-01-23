package com.project.project_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 1. 允许携带 Cookie (必须 true)
        config.setAllowCredentials(true);
        
        // 2. 允许的域名 (把你的域名都填上)
        config.setAllowedOriginPatterns(Arrays.asList(
            "https://xmgl.lgyt304.shop",       // 你的前端
            "https://*.lgyt304.shop",
            "https://*.pages.dev",
            "http://localhost:5173",
            "http://localhost:8080"
        ));
        
        // 3. 允许的头信息 (上传文件会有特殊的 Content-Type，建议全开)
        config.addAllowedHeader("*");
        
        // 4. 允许的方法
        config.addAllowedMethod("*");
        
        // 5. 暴露的头信息 (方便前端读取)
        config.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用这个配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
