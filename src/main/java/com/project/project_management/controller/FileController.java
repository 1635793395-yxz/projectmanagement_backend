package com.project.project_management.controller;

import com.project.project_management.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    // 简单的本地上传实现
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("文件为空");

        // 1. 确定文件名 (UUID防止重名)
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 2. 确定保存路径 (这里保存在项目根目录的 uploads 文件夹下)
        // 注意：生产环境通常用 OSS 或指定绝对路径
        String projectPath = System.getProperty("user.dir");
        String savePath = projectPath + File.separator + "uploads";
        File folder = new File(savePath);
        if (!folder.exists()) folder.mkdirs();

        try {
            // 3. 保存文件
            file.transferTo(new File(folder, newFileName));

            // 4. 返回可访问的 URL (假设后端配置了静态资源映射，或者前端直接拼路径)
            return Result.success("/uploads/" + newFileName);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}