package com.project.project_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取当前项目运行的根目录
        String projectPath = System.getProperty("user.dir");

        // 当访问 URL 为 /uploads/** 时
        // 映射到本地磁盘路径 projectPath/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + projectPath + File.separator + "uploads" + File.separator);
    }
}