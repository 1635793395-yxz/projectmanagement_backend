package com.project.project_management.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class CaptchaController {

    // 生成验证码图片接口
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int width = 100;
        int height = 40;

        // 1. 创建图片对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 2. 填充背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 3. 画干扰线 (防止机器识别)
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 4. 生成 4 位随机数
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        g.setFont(new Font("Arial", Font.BOLD, 24));

        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(chars.length());
            char c = chars.charAt(index);
            sb.append(c);

            // 设置随机颜色
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            // 画字符
            g.drawString(String.valueOf(c), 20 * i + 10, 30);
        }

        // 5. 将验证码存入 Session (用于后续校验)
        HttpSession session = request.getSession();
        session.setAttribute("CAPTCHA_CODE", sb.toString());

        // 6. 输出图片给浏览器
        response.setContentType("image/jpeg");
        // 禁止缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        ImageIO.write(image, "JPEG", response.getOutputStream());
    }
}